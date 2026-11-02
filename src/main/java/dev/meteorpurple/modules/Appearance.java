package dev.meteorpurple.modules;

import dev.meteorpurple.PurpleAddon;
import dev.meteorpurple.hud.PurpleHud;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinAccentColor;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.CatppuccinFlavors;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;

public final class Appearance extends Module {
    public enum Style {
        Mocha("Catppuccin Mocha"), Glass("Purple Glass"), Minimal("Minimal Lavender");
        private final String title;
        Style(String title) { this.title = title; }
        @Override public String toString() { return title; }
    }

    private boolean pending = true;
    private final SettingGroup general = settings.getDefaultGroup();
    public final Setting<Style> style = general.add(new EnumSetting.Builder<Style>()
        .name("theme").description("Apply a coordinated Catppuccin UI and HUD profile without restarting.")
        .defaultValue(Style.Mocha).onChanged(v -> pending = true).build());
    public final Setting<Boolean> performance = general.add(new BoolSetting.Builder()
        .name("performance-mode").description("Disable shadows and animations and use opaque panels.")
        .defaultValue(false).onChanged(v -> pending = true).build());
    public final Setting<Integer> blurRadius = general.add(new IntSetting.Builder()
        .name("glass-blur-radius").description("Minecraft framebuffer blur radius. Zero disables it; performance mode bypasses blur.")
        .defaultValue(6).range(0, 10).sliderRange(0, 10).visible(() -> style.get() == Style.Glass).build());
    private final Setting<Double> scale = general.add(new DoubleSetting.Builder()
        .name("gui-scale").description("Scale of the real Catppuccin widgets.")
        .defaultValue(1).range(.75, 4).sliderRange(.75, 3).onSliderRelease().onChanged(v -> pending = true).build());
    private final Setting<Double> opacity = general.add(new DoubleSetting.Builder()
        .name("panel-opacity").description("Maximum panel opacity. Glass uses a softer multiplier.")
        .defaultValue(1).range(.15, 1).sliderRange(.15, 1).onChanged(v -> pending = true).build());
    private final Setting<Integer> duration = general.add(new IntSetting.Builder()
        .name("animation-ms").description("Animation duration; performance and Minimal modes disable animations.")
        .defaultValue(240).range(1, 1000).sliderRange(1, 600).onChanged(v -> pending = true).build());

    public Appearance() {
        super(PurpleAddon.CATEGORY, "purple-appearance", "Three live appearance profiles for the actual Catppuccin addon. Settings apply even while this control module is off.");
        runInMainMenu = true;
    }

    public void applyPending() {
        if (!pending) return;
        if (!(GuiThemes.get() instanceof CatppuccinGuiTheme)) {
            // Select only on first application or an explicit profile change.
            GuiThemes.select("Catppuccin");
        }
        if (!(GuiThemes.get() instanceof CatppuccinGuiTheme theme)) return;
        boolean minimal = style.get() == Style.Minimal;
        boolean low = performance.get() || minimal;
        theme.flavor.set(CatppuccinFlavors.Mocha);
        setAccent(theme, style.get() == Style.Glass ? CatppuccinAccentColor.Mauve : CatppuccinAccentColor.Lavender);
        theme.scale.set(Math.max(.75, scale.get() * (minimal ? .85 : 1)));
        theme.windowOpacity.set(performance.get() ? 1 : opacity.get() * (style.get() == Style.Glass ? .72 : 1));
        theme.backgroundOpacity.set(performance.get() ? 1 : opacity.get() * (style.get() == Style.Glass ? .65 : 1));
        theme.cornerRadius.set(minimal ? 3 : 12);
        theme.smallCornerRadius.set(minimal ? 2 : 6);
        theme.windowShadow.set(!low);
        theme.shadowBlur.set(low ? 0 : 36);
        theme.shadowSpread.set(-8);
        theme.guiAnimationDuration.set(low ? 1 : duration.get());
        theme.catppuccinSearchScreen.set(true);
        theme.snapModuleCategories.set(true);
        theme.reloadScreen();
        pending = false;
    }

    @SuppressWarnings("unchecked")
    private void setAccent(CatppuccinGuiTheme theme, CatppuccinAccentColor accent) {
        // Public Meteor settings API; Catppuccin keeps the backing accent field private.
        Setting<?> setting = theme.settings.get("accent");
        if (setting != null) ((Setting<CatppuccinAccentColor>) setting).set(accent);
    }

    public Color accent() { return style.get() == Style.Glass ? GLASS_ACCENT : LAVENDER; }
    public boolean glassBlurEnabled() { return style.get() == Style.Glass && !performance.get() && blurRadius.get() > 0; }
    public Color background() { return style.get() == Style.Glass ? GLASS_BASE : MOCHA_BASE; }
    private static final Color LAVENDER = new Color(180, 190, 254);
    private static final Color GLASS_ACCENT = new Color(203, 166, 247);
    private static final Color MOCHA_BASE = new Color(30, 30, 46);
    private static final Color GLASS_BASE = new Color(44, 24, 70);

    @Override public WWidget getWidget(GuiTheme theme) {
        var list = theme.verticalList();
        var apply = list.add(theme.button("Apply appearance now")).expandX().widget();
        apply.action = () -> { pending = true; applyPending(); };
        var install = list.add(theme.button("Add missing Purple HUD elements")).expandX().widget();
        install.action = PurpleHud::installLayout;
        list.add(theme.label("Drag and configure each element in Meteor's HUD editor."));
        return list;
    }
}

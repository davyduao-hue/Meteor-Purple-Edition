package dev.meteorpurple.modules;

import dev.meteorpurple.PurpleAddon;
import dev.meteorpurple.hud.PurpleHud;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinAccentColor;
import me.pindour.catppuccin.gui.themes.catppuccin.flavors.CatppuccinFlavors;
import meteordevelopment.meteorclient.gui.*;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.mixin.MinecraftAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;

public final class Appearance extends Module {
    public enum Style {
        Mocha("Catppuccin Mocha","Velvet charcoal and lavender",0x1e1e2e,0x181825,0xb4befe,0xcdd6f4,false,false),
        Glass("Purple Glass","Soft purple, floating translucent panels",0x21162f,0x160e22,0xcba6f7,0xe9dff8,false,true),
        Minimal("Minimal Lavender","Compact, quiet and lightweight",0x181821,0x101017,0xb9adf8,0xe6e1f5,false,false),
        OS26("OS26 Glass","Pearl glass and lilac light",0xeff2fc,0xdde4f5,0x7964d8,0x202238,true,true),
        Midnight("Midnight","Inky indigo and electric violet",0x111326,0x0b0d19,0x9b8dff,0xe3e7ff,false,false),
        Rose("Rose Quartz","Warm plum and soft rose",0x281c29,0x1a121c,0xf2a9c2,0xf7e5ed,false,false),
        Ocean("Ocean","Deep navy and glacial blue",0x101f2c,0x0a1520,0x80cee8,0xddf3fb,false,false),
        Forest("Forest","Evergreen and mint accents",0x14231f,0x0d1815,0x94d7b2,0xe1f3e9,false,false),
        Amoled("AMOLED Violet","True black, crisp violet",0x000000,0x000000,0xbc9aff,0xece6fa,false,false),
        Original("Original Meteor","Meteor's original interface",0x202020,0x141414,0x913de2,0xffffff,false,false);
        public final String title,subtitle;
        public final Palette palette;
        public final boolean glass;
        Style(String title,String subtitle,int base,int mantle,int accent,int text,boolean light,boolean glass) {
            this.title=title; this.subtitle=subtitle; palette=new Palette(base,mantle,accent,text,light); this.glass=glass;
        }
        @Override public String toString() { return title; }
    }
    private static Appearance instance;
    public static Appearance get() { return instance; }
    private boolean pending=true,adaptiveSuppressed;
    private int sampleTicks;
    private final SettingGroup general=settings.getDefaultGroup();
    public final Setting<Style> style=general.add(new EnumSetting.Builder<Style>().name("theme")
        .description("Ten live, coordinated UI/menu/HUD palettes.").defaultValue(Style.OS26).onChanged(v -> pending=true).build());
    public final Setting<Boolean> customMenu=general.add(new BoolSetting.Builder().name("custom-main-menu")
        .description("Use the Purple home screen; disable to restore Minecraft's menu.").defaultValue(true).build());
    public final Setting<Boolean> performance=general.add(new BoolSetting.Builder().name("performance-mode")
        .description("Skip blur, shadows and animation; use opaque panels and a lower HUD refresh rate.")
        .defaultValue(false).onChanged(v -> pending=true).build());
    public final Setting<Boolean> reducedMotion=general.add(new BoolSetting.Builder().name("reduced-motion")
        .description("Disable transitions while preserving transparency.").defaultValue(false).onChanged(v -> pending=true).build());
    public final Setting<Boolean> adaptiveBlur=general.add(new BoolSetting.Builder().name("adaptive-blur")
        .description("Suspend blur below 45 FPS; restore above 60 FPS, sampled once per second.").defaultValue(true).build());
    public final Setting<Integer> blurRadius=general.add(new IntSetting.Builder().name("glass-blur-radius")
        .description("Native blur radius; zero skips the entire pass.").defaultValue(4).range(0,10).sliderRange(0,10)
        .visible(() -> style.get().glass).build());
    private final Setting<Double> scale=general.add(new DoubleSetting.Builder().name("gui-scale")
        .description("Widget scale, preserved across palettes.").defaultValue(1).range(.75,4).sliderRange(.75,3)
        .onSliderRelease().onChanged(v -> pending=true).build());
    private final Setting<Double> opacity=general.add(new DoubleSetting.Builder().name("panel-opacity")
        .description("Opacity multiplier; glass has softer surfaces.").defaultValue(1).range(.25,1).sliderRange(.25,1)
        .onChanged(v -> pending=true).build());
    private final Setting<Integer> duration=general.add(new IntSetting.Builder().name("animation-ms")
        .description("Transition duration; reduced motion bypasses it.").defaultValue(170).range(1,700).sliderRange(1,400)
        .onChanged(v -> pending=true).build());
    public Appearance() {
        super(PurpleAddon.CATEGORY,"purple-appearance","Themes, home screen, classic UI and rendering budget. Controls work while off.");
        instance=this; runInMainMenu=true;
    }
    public void select(Style selected) { style.set(selected); applyPending(); }
    public void applyPending() {
        if (++sampleTicks>=20) {
            sampleTicks=0; int fps=MinecraftAccessor.meteor$getFps();
            if (fps>0 && fps<45) adaptiveSuppressed=true; else if (fps>=60) adaptiveSuppressed=false;
        }
        if (!pending) return;
        Style selected=style.get(); String target=selected==Style.Original ? "Meteor":"Catppuccin";
        if (GuiThemes.get()==null || !GuiThemes.get().name.equals(target)) GuiThemes.select(target);
        if (selected==Style.Original) {
            pending=false;
            return;
        }
        if (!(GuiThemes.get() instanceof CatppuccinGuiTheme theme)) return;
        boolean compact=selected==Style.Minimal,low=performance.get() || compact || selected==Style.Amoled;
        theme.flavor.set(selected==Style.OS26 ? CatppuccinFlavors.Latte:CatppuccinFlavors.Mocha);
        theme.settings.get("accent",CatppuccinAccentColor.class).set(CatppuccinAccentColor.Lavender);
        theme.scale.set(Math.max(.75,scale.get()*(compact ? .9:1)));
        theme.windowOpacity.set(performance.get() ? 1:opacity.get()*(selected.glass ? .82:1));
        theme.backgroundOpacity.set(performance.get() ? 1:opacity.get()*(selected.glass ? .68:1));
        theme.cornerRadius.set(compact ? 5:selected==Style.OS26 ? 18:12);
        theme.smallCornerRadius.set(compact ? 3:selected==Style.OS26 ? 10:6);
        theme.windowShadow.set(!low); theme.shadowBlur.set(low ? 0:selected.glass ? 22:14);
        theme.shadowSpread.set(-5); theme.shadowOffsetY.set(selected.glass ? 4:2);
        theme.guiAnimationDuration.set(low || reducedMotion.get() ? 1:duration.get());
        theme.catppuccinSearchScreen.set(true); theme.snapModuleCategories.set(true);
        pending=false;
        theme.reloadScreen();
    }
    public Palette palette() { return style.get().palette; }
    public Color accent() { return palette().accent; }
    public Color background() { return palette().base; }
    public boolean glassBlurEnabled() { return style.get().glass && !performance.get() && blurRadius.get()>0 && (!adaptiveBlur.get() || !adaptiveSuppressed); }
    public boolean animate() { return !performance.get() && !reducedMotion.get() && style.get()!=Style.Minimal && style.get()!=Style.Amoled; }
    @Override public WWidget getWidget(GuiTheme theme) {
        var list=theme.verticalList();
        list.add(theme.button("Browse theme gallery")).expandX().widget().action=() -> mc.gui.setScreen(new ThemeGalleryScreen(mc.gui.screen()));
        list.add(theme.button("Apply appearance now")).expandX().widget().action=() -> { pending=true; applyPending(); };
        list.add(theme.button("Add missing Purple HUD elements")).expandX().widget().action=PurpleHud::installLayout;
        return list;
    }
}

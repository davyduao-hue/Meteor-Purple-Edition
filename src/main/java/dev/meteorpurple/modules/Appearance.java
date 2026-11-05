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
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.nbt.CompoundTag;

public final class Appearance extends Module {
    public enum Style {
        Mocha("Velvet Purple","Plum-black surfaces and luminous lilac",0x171222,0x100e1b,0xc49aff,0xf4efff,false,false),
        Glass("Amethyst Glass","Soft translucent layers and gem icons",0x191525,0x0f0d17,0xdab8ff,0xf4edff,false,true),
        Minimal("Quiet Lavender","Fine outlines, restrained motion, compact shapes",0x17161c,0x101014,0xbba8e8,0xf1eef9,false,false),
        OS26("OS26 Pearl Glass","Pearl surfaces, purple glass controls and disc icons",0xf1edf8,0xe3deef,0x7952cc,0x2b213c,true,true),
        Midnight("Midnight Neon","Angular neon details over deep indigo",0x0e1022,0x080a17,0xa69aff,0xedf0ff,false,false),
        Rose("Rose Silk","Soft capsules, plum surfaces and rose light",0x281423,0x1b0e19,0xf1aacd,0xf7eaf3,false,false),
        Ocean("Tidal Blue","Glacial accents, tidal icon arcs and navy",0x0d1d2b,0x08131e,0x87daf0,0xe8f8ff,false,false),
        Forest("Mint Grove","Leaf-cut icons and calm evergreen panels",0x11221d,0x0a1612,0x9fdfc1,0xebfff4,false,false),
        Amoled("Obsidian OLED","True black, thin violet outlines and no idle effects",0x000000,0x000000,0xc6a4ff,0xf2edff,false,false),
        Original("Classic Meteor","Sharp graphite and saturated Meteor purple",0x1d1921,0x100e13,0xb66bff,0xf1eaf7,false,false);
        public final String title,subtitle;
        public final Palette palette;
        public final boolean glass;
        Style(String title,String subtitle,int base,int mantle,int accent,int text,boolean light,boolean glass) {
            this.title=title; this.subtitle=subtitle; palette=new Palette(base,mantle,accent,text,light); this.glass=glass;
        }
        @Override public String toString() { return title; }
    }
    public enum Backdrop { Meteor, Colosseum }
    public enum Motion { Gentle, Playful, Arcade }
    private static Appearance instance;
    public static Appearance get() { return instance; }
    private boolean pending=true,adaptiveSuppressed;
    private int sampleTicks;
    private final SettingGroup general=settings.getDefaultGroup();
    public final Setting<Style> style=general.add(new EnumSetting.Builder<Style>().name("theme")
        .description("Ten live, coordinated UI/menu/HUD palettes.").defaultValue(Style.Mocha).onChanged(v -> pending=true).build());
    public final Setting<Boolean> customClientUi=general.add(new BoolSetting.Builder().name("custom-client-ui")
        .description("Revamped classic category windows, settings, icons and animation. Turn off for the native Meteor theme.")
        .defaultValue(true).onChanged(v -> pending=true).build());
    public final Setting<Boolean> roundedTypeface=general.add(new BoolSetting.Builder().name("zenith-typeface")
        .description("Use bundled rounded Fredoka in client screens. Turn off to use Meteor's chosen font; does not change that preference.")
        .defaultValue(true).onChanged(v -> pending=true).build());
    public final Setting<Boolean> customMenu=general.add(new BoolSetting.Builder().name("custom-main-menu")
        .description("Clean Meteor-logo menu with optional colosseum backdrop and springy interaction.").defaultValue(true).build());
    public final Setting<Backdrop> menuBackground=general.add(new EnumSetting.Builder<Backdrop>().name("menu-background")
        .description("Meteor logo is the default; Colosseum is an optional captured panorama.").defaultValue(Backdrop.Meteor)
        .onChanged(v -> ColosseumPanorama.select(v==Backdrop.Colosseum)).build());
    public final Setting<Boolean> uiAnimations=general.add(new BoolSetting.Builder().name("ui-animations")
        .description("Springy menu buttons, floating logo and expressive loading feedback.").defaultValue(true).onChanged(v -> { pending=true; saveMotion(); }).build());
    public final Setting<Motion> motionStyle=general.add(new EnumSetting.Builder<Motion>().name("motion-style")
        .description("Gentle is restrained; Playful adds bounce; Arcade adds extra movement and motes.")
        .defaultValue(Motion.Playful).onChanged(v -> saveMotion()).build());
    public final Setting<Boolean> particles=general.add(new BoolSetting.Builder().name("ui-particles")
        .description("Bounded ambient motes, hover sparks and loading bursts. Never spawns world entities.")
        .defaultValue(true).onChanged(v -> saveMotion()).build());
    public final Setting<Boolean> gameAnimations=general.add(new BoolSetting.Builder().name("in-game-animations")
        .description("Optional Zenith HUD entrance/pulses and springy pause/options buttons. No camera shake or gameplay changes.")
        .defaultValue(false).onChanged(v -> saveMotion()).build());
    public final Setting<Boolean> menuCredits=general.add(new BoolSetting.Builder().name("menu-addon-credits")
        .description("Show Meteor's addon credit overlay on the custom main menu.").defaultValue(false).build());
    public final Setting<Boolean> performance=general.add(new BoolSetting.Builder().name("performance-mode")
        .description("Skip blur, shadows and animation; use opaque panels and a lower HUD refresh rate.")
        .defaultValue(false).onChanged(v -> { pending=true; saveMotion(); }).build());
    public final Setting<Boolean> reducedMotion=general.add(new BoolSetting.Builder().name("reduced-motion")
        .description("Disable transitions while preserving transparency.").defaultValue(false).onChanged(v -> { pending=true; saveMotion(); }).build());
    public final Setting<Boolean> adaptiveBlur=general.add(new BoolSetting.Builder().name("adaptive-blur")
        .description("Suspend blur below 45 FPS; restore above 60 FPS, sampled once per second.").defaultValue(true).build());
    public final Setting<Integer> blurRadius=general.add(new IntSetting.Builder().name("glass-blur-radius")
        .description("Native blur radius; zero skips the entire pass.").defaultValue(0).range(0,10).sliderRange(0,10)
        .visible(() -> style.get().glass).build());
    public final Setting<Boolean> softShadows=general.add(new BoolSetting.Builder().name("soft-shadows")
        .description("Optional Catppuccin window shadows. Off by default to reduce draw work.").defaultValue(false).onChanged(v -> pending=true).build());
    public final Setting<Boolean> useCustomAccent=general.add(new BoolSetting.Builder().name("custom-accent")
        .description("Choose your own menu underline, UI and HUD accent.").defaultValue(false).onChanged(v -> pending=true).build());
    public final Setting<SettingColor> accentColor=general.add(new ColorSetting.Builder().name("accent-color")
        .description("Custom accent; text keeps its readable theme color.").defaultValue(new SettingColor(180,166,247))
        .visible(() -> useCustomAccent.get()).onChanged(v -> pending=true).build());
    private boolean starterHudInstalled;
    private Style cachedStyle;
    private int cachedAccent;
    private Palette customPalette;
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
        super(PurpleAddon.CATEGORY,"purple-appearance","Themes, vanilla-style menu, Zenith HUD and rendering budget. Controls work while off.");
        instance=this; runInMainMenu=true;
    }
    public void select(Style selected) { style.set(selected); applyPending(); }
    public void applyPending() {
        if (++sampleTicks>=20) {
            sampleTicks=0; int fps=MinecraftAccessor.meteor$getFps();
            if (fps>0 && fps<45) adaptiveSuppressed=true; else if (fps>=60) adaptiveSuppressed=false;
        }
        if (!pending) return;
        Style selected=style.get(); String target=customClientUi.get() ? "Catppuccin":"Meteor";
        if (GuiThemes.get()==null || !GuiThemes.get().name.equals(target)) GuiThemes.select(target);
        if (!customClientUi.get()) {
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
        theme.cornerRadius.set(ThemeDesign.of(selected).windowRadius());
        theme.smallCornerRadius.set(ThemeDesign.of(selected).controlRadius());
        theme.windowShadow.set(!low && softShadows.get()); theme.shadowBlur.set(low || !softShadows.get() ? 0:10);
        theme.shadowSpread.set(-5); theme.shadowOffsetY.set(selected.glass ? 4:2);
        theme.guiAnimationDuration.set(low || reducedMotion.get() || !uiAnimations.get() ? 1:duration.get());
        theme.catppuccinSearchScreen.set(true); theme.snapModuleCategories.set(true);
        theme.modulesHelpText.set(false); theme.modulesWatermark.set(false);
        pending=false;
        theme.reloadScreen();
    }
    public Palette palette() {
        Style selected=style.get();
        if(!useCustomAccent.get()) return selected.palette;
        Color color=accentColor.get(); int rgb=color.r<<16 | color.g<<8 | color.b;
        if(customPalette==null || cachedStyle!=selected || cachedAccent!=rgb) {
            cachedStyle=selected; cachedAccent=rgb;
            Palette p=selected.palette;
            customPalette=new Palette(p.base,p.mantle,color,p.text,p.light);
        }
        return customPalette;
    }
    public int animationMs() { return duration.get(); }
    public void installStarterHudOnce() {
        if(starterHudInstalled) return;
        PurpleHud.installStarterLayout(); starterHudInstalled=true;
    }
    @Override public CompoundTag toTag() {
        CompoundTag tag=super.toTag(); tag.putBoolean("zenith-hud-installed",starterHudInstalled); return tag;
    }
    @Override public Module fromTag(CompoundTag tag) {
        starterHudInstalled=tag.getBoolean("zenith-hud-installed").orElse(false); return super.fromTag(tag);
    }
    public Color accent() { return palette().accent; }
    public Color background() { return palette().base; }
    public boolean glassBlurEnabled() { return style.get().glass && !performance.get() && blurRadius.get()>0 && (!adaptiveBlur.get() || !adaptiveSuppressed); }
    private boolean motionAllowed() { return !performance.get() && !reducedMotion.get() && style.get()!=Style.Minimal && style.get()!=Style.Amoled; }
    public boolean animate() { return uiAnimations.get() && motionAllowed(); }
    public boolean animateGame() { return gameAnimations.get() && motionAllowed(); }
    private void saveMotion() {
        if(instance!=this || uiAnimations==null || gameAnimations==null || reducedMotion==null || performance==null || motionStyle==null || particles==null) return;
        MotionPrefs.save(uiAnimations.get(),gameAnimations.get(),reducedMotion.get(),performance.get(),motionStyle.get().ordinal(),particles.get());
    }
    @Override public WWidget getWidget(GuiTheme theme) {
        var list=theme.verticalList();
        list.add(theme.button("Browse theme gallery")).expandX().widget().action=() -> mc.gui.setScreen(new ThemeGalleryScreen(mc.gui.screen()));
        list.add(theme.button("Apply appearance now")).expandX().widget().action=() -> { pending=true; applyPending(); };
        list.add(theme.button("Add Zenith HUD bars")).expandX().widget().action=PurpleHud::installStarterLayout;
        return list;
    }
}

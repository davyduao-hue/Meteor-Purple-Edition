package qa;

import dev.meteorpurple.modules.*;
import dev.meteorpurple.ui.*;
import dev.meteorpurple.hud.PurpleHud;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import java.nio.file.*;

public class PurpleSmoke implements ClientModInitializer {
    private int ticks,step=-1;
    private String shot;
    private boolean captureReload,overlayCaptured;
    private int overlayTicks;
    private final Path out=Path.of("screenshots");
    public void onInitializeClient() {
        try { Files.createDirectories(out); } catch(Exception e) { throw new RuntimeException(e); }
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }
    private void tick(Minecraft mc) {
        if(mc.gui.overlay()!=null) {
            if(captureReload && !overlayCaptured && ++overlayTicks==22) {
                overlayCaptured=true;
                Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),image -> {
                    try { image.writeToFile(out.resolve("zenith-loading.png")); } catch(Exception e) { throw new RuntimeException(e); } finally { image.close(); }
                });
            }
            return;
        }
        if(Appearance.get()==null) return;
        if(step==21 && mc.player==null) {
            if(mc.gui.screen() instanceof net.minecraft.client.gui.screens.LevelLoadingScreen && !java.nio.file.Files.exists(out.resolve("zenith-world-loading.png"))) {
                Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),image -> {
                    try { image.writeToFile(out.resolve("zenith-world-loading.png")); } catch(Exception e) { throw new RuntimeException(e); } finally { image.close(); }
                });
            }
            return;
        }
        if(mc.gui.screen()==null && mc.player==null) return;
        if(++ticks<45) return;
        ticks=0;
        if(shot!=null) {
            Path image=out.resolve(shot+".png");
            Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),nativeImage -> {
                try { nativeImage.writeToFile(image); } catch(Exception e) { throw new RuntimeException(e); } finally { nativeImage.close(); }
            });
            System.out.println("PURPLE_SMOKE captured="+shot+" screen="+(mc.gui.screen()==null ? "World":mc.gui.screen().getClass().getSimpleName()));
        }
        Appearance a=Appearance.get(); step++;
        if(step==0) {
            var loader=FabricLoader.getInstance();
            boolean withAddons=loader.isModLoaded("glazed");
            if(withAddons) {
                require(loader.isModLoaded("exploitpreventer"),"Glazed nested EP");
                require(loader.isModLoaded("opsec"),"official OpSec");
                try {
                    var c=Class.forName("aurick.opsec.mod.config.OpsecConfig");
                    require(c.getField("EXPLOIT_PREVENTER_LOADED").getBoolean(null),"upstream EP compatibility active");
                    c.getMethod("getInstance").invoke(null);
                } catch(Exception e) { throw new RuntimeException(e); }
            }
            a.performance.set(false); a.reducedMotion.set(false); a.blurRadius.set(0); a.select(Appearance.Style.Mocha); a.customMenu.set(true); a.useCustomAccent.set(false);
            require(!a.glassBlurEnabled(),"no default blur");
            mc.gui.setScreen(new TitleScreen());
            require(mc.gui.screen() instanceof PurpleTitleScreen,"custom menu route");
            require(Modules.get().getCount()>100,"Meteor modules"); shot="menu-mocha";
        } else if(step==1) {
            Button single=find(mc,"Singleplayer"); require(single!=null,"vanilla singleplayer button"); single.onPress(null);
            require(!(mc.gui.screen() instanceof TitleScreen),"native singleplayer action changed screen");
            System.out.println("PURPLE_SMOKE singleplayer native screen="+mc.gui.screen().getClass().getName()); shot="singleplayer";
        } else if(step==2) {
            mc.gui.setScreen(new TitleScreen()); Button options=find(mc,"Options...");
            if(options==null) options=find(mc,"Options\u2026");
            require(options!=null,"vanilla options button"); options.onPress(null);
            require(mc.gui.screen() instanceof OptionsScreen,"native options action"); shot="options";
        } else if(step==3) {
            mc.gui.setScreen(new TitleScreen()); Button themes=find(mc,"Themes"); require(themes!=null,"theme button"); themes.onPress(null);
            require(mc.gui.screen() instanceof ThemeGalleryScreen,"gallery action"); shot="theme-gallery";
        } else if(step>=4 && step<14) {
            var style=Appearance.Style.values()[step-4]; a.select(style);
            mc.gui.setScreen(GuiThemes.get().modulesScreen());
            shot="classic-"+style.name().toLowerCase();
        } else if(step==14) {
            a.select(Appearance.Style.Mocha); a.useCustomAccent.set(true); a.accentColor.set(new meteordevelopment.meteorclient.utils.render.color.SettingColor(120,220,190));
            require(a.palette().accent.g==220,"custom accent palette");
            mc.gui.setScreen(new TitleScreen()); find(mc,"Singleplayer").setFocused(true); shot="menu-custom-hover";
        } else if(step==15) {
            a.useCustomAccent.set(false); a.select(Appearance.Style.Mocha);
            Hud.get().clear(); PurpleHud.installStarterLayout(); PurpleHud.installStarterLayout();
            int count=0; for(var e:Hud.get()) count++; require(count==2,"HUD install is idempotent");
            mc.gui.setScreen(new HudEditorScreen(GuiThemes.get())); shot="zenith-hud-editor";
        } else if(step==16) {
            HudElement metrics=null,brand=null;
            for(var e:Hud.get()) { if(e.info==PurpleHud.info(PurpleHud.Kind.Performance)) metrics=e; else brand=e; }
            require(metrics!=null && brand!=null,"two HUD bars");
            require(metrics.box.yAnchor==YAnchor.Bottom,"bottom anchor");
            require(metrics.y+metrics.getHeight()<=mc.getWindow().getHeight(),"metrics inside viewport");
            var tag=brand.toTag(); brand.toggle(); require(!brand.isActive(),"HUD toggle off"); brand.fromTag(tag); require(brand.isActive(),"HUD settings restoration");
            metrics.settings.get("frame-time",Boolean.class).set(false); metrics.settings.get("heap-memory",Boolean.class).set(false);
            mc.gui.setScreen(new HudEditorScreen(GuiThemes.get())); shot="hud-metric-toggles";
        } else if(step==17) {
            a.performance.set(true); a.select(Appearance.Style.OS26); require(!a.glassBlurEnabled(),"performance blur bypass"); require(!a.animate(),"performance animation bypass");
            mc.gui.setScreen(new TitleScreen()); find(mc,"Singleplayer").setFocused(true); shot="menu-performance";
        } else if(step==18) {
            if(FabricLoader.getInstance().isModLoaded("opsec")) {
                Modules.get().get(Privacy.class).getWidget(GuiThemes.get());
                try { mc.gui.setScreen((net.minecraft.client.gui.screens.Screen)Class.forName("aurick.opsec.mod.config.OpsecConfigScreen").getConstructor(net.minecraft.client.gui.screens.Screen.class).newInstance(mc.gui.screen())); }
                catch(Exception e) { throw new RuntimeException(e); }
            } else mc.gui.setScreen(new JoinMultiplayerScreen(mc.gui.screen()));
            shot="addon-settings";
        } else if(step==19) {
            a.customMenu.set(false); mc.gui.setScreen(new TitleScreen()); require(mc.gui.screen().getClass()==TitleScreen.class,"vanilla fallback"); shot="vanilla-fallback";
        } else if(step==20) {
            a.customMenu.set(true); a.performance.set(false); a.select(Appearance.Style.Mocha);
            mc.gui.setScreen(new TitleScreen()); captureReload=true; mc.reloadResourcePacks(); shot=null;
        } else if(step==21 && java.nio.file.Files.exists(java.nio.file.Path.of("saves/Zenith-Colosseum/level.dat"))) {
            require(overlayCaptured,"real branded resource-reload overlay captured");
            mc.createWorldOpenFlows().openWorld("Zenith-Colosseum",() -> {}); shot=null;
        } else if(step==22 && mc.player!=null) {
            require(mc.level!=null,"real colosseum world joined");
            mc.getSingleplayerServer().execute(() -> mc.getSingleplayerServer().getCommands().performPrefixedCommand(mc.getSingleplayerServer().createCommandSourceStack(),"setworldspawn 0 76 12"));
            shot="zenith-world-hud";
        } else {
            require(overlayCaptured,"real branded resource-reload overlay captured");
            shot=null;
            try { Files.writeString(out.resolve("RESULT.txt"),"PASS: native menu actions, ten classic palettes, custom accent, editable anchored HUD and toggles, performance mode, optional addon status/settings. Modules: "+Modules.get().getCount()); }
            catch(Exception e) { throw new RuntimeException(e); }
            System.out.println("PURPLE_SMOKE PASS modules="+Modules.get().getCount()); mc.stop();
        }
    }
    private static Button find(Minecraft mc,String title) {
        for(var child:mc.gui.screen().children()) if(child instanceof Button b && b.getMessage().getString().equals(title)) return b;
        return null;
    }
    private static void require(boolean condition,String label) { if(!condition) throw new AssertionError(label); }
}

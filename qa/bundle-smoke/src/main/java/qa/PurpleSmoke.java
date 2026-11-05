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
    private int preview=-1,showcase,showTicks;
    private String shot;
    private boolean captureReload,overlayCaptured;
    private int overlayTicks;
    private final Path out=Path.of("screenshots");
    public void onInitializeClient() {
        if(Boolean.getBoolean("zenith.polish")) { LoadingPolish.start(); return; }
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
        if(preview>=0 && preview<65) {
            if(preview==22) { Button b=find(mc,"Singleplayer"); if(b!=null) b.setFocused(true); }
            if(preview==45) {
                Button b=find(mc,"Singleplayer"); if(b!=null) b.setFocused(false);
                float hero=mc.getWindow().getGuiScaledHeight()>=300 ? Math.max(56,(mc.getWindow().getGuiScaledHeight()-290)/2f+44):56;
                mc.gui.screen().mouseClicked(new net.minecraft.client.input.MouseButtonEvent(mc.getWindow().getGuiScaledWidth()/2f,hero,new net.minecraft.client.input.MouseButtonInfo(0,0)),false);
            }
            final int frame=preview++;
            try { Files.createDirectories(out.resolve("motion")); } catch(Exception e) { throw new RuntimeException(e); }
            Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),image -> {
                try { image.writeToFile(out.resolve("motion/frame"+String.format("%03d",frame)+".png")); } catch(Exception e) { throw new RuntimeException(e); } finally { image.close(); }
            });
            return;
        }
        if(step==0 && preview>=65 && showcase<2) {
            if(showcase==0 && showTicks==0) mc.gui.setScreen(new ThemeGalleryScreen(mc.gui.screen()));
            if(++showTicks==35) {
                capture(mc,showcase==0 ? "theme-gallery":"motion-settings"); showcase++; showTicks=0;
                if(showcase==1) mc.gui.setScreen(new MotionScreen(new TitleScreen()));
                else mc.gui.setScreen(new TitleScreen());
            }
            return;
        }
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
            a.customClientUi.set(true); a.roundedTypeface.set(true); a.motionStyle.set(Appearance.Motion.Playful); a.particles.set(true); a.performance.set(false); a.reducedMotion.set(false); a.blurRadius.set(0); a.uiAnimations.set(true); a.gameAnimations.set(false); a.menuBackground.set(Appearance.Backdrop.Meteor); a.select(Appearance.Style.Mocha); a.customMenu.set(true); a.useCustomAccent.set(false);
            require(!a.glassBlurEnabled(),"no default blur");
            require(a.menuBackground.get()==Appearance.Backdrop.Meteor,"Meteor backdrop default");
            require(!a.gameAnimations.get(),"game animations opt in");
            mc.gui.setScreen(new TitleScreen());
            require(mc.gui.screen() instanceof PurpleTitleScreen,"custom menu route");
            a.uiAnimations.set(false); require(!a.animate(),"UI motion off");
            a.uiAnimations.set(true); require(a.animate(),"UI motion on");
            a.gameAnimations.set(true); a.reducedMotion.set(true); require(!a.animateGame(),"reduced motion overrides game feedback");
            a.reducedMotion.set(false); a.gameAnimations.set(false); preview=0;
            RenderBudget.run(mc,out); ClientUiAudit.run(mc);
            var original=mc.gui.screen(); find(mc,"Motion").onPress(null);
            require(mc.gui.screen() instanceof MotionScreen,"quick motion screen");
            find(mc,"Arcade").onPress(null); require(a.motionStyle.get()==Appearance.Motion.Arcade,"Arcade selection");
            find(mc,"Performance").onPress(null); require(!a.animate(),"quick performance override");
            find(mc,"Playful").onPress(null); require(a.animate() && !a.performance.get(),"preset restores effects");
            find(mc,"Reduce motion").onPress(null); require(!a.animate(),"quick reduced motion override");
            find(mc,"Gentle").onPress(null); require(a.animate(),"Gentle selection");
            a.motionStyle.set(Appearance.Motion.Playful); mc.gui.setScreen(original);
            require(Modules.get().getCount()>100,"Meteor modules"); shot="menu-mocha";
        } else if(step==1) {
            Button single=find(mc,"Singleplayer"); require(single!=null,"vanilla singleplayer button"); single.onPress(null);
            require(!(mc.gui.screen() instanceof TitleScreen),"native singleplayer action changed screen");
            System.out.println("PURPLE_SMOKE singleplayer native screen="+mc.gui.screen().getClass().getName()); shot="singleplayer";
        } else if(step==2) {
            mc.gui.setScreen(new TitleScreen()); Button options=find(mc,"Options...");
            if(options==null) options=find(mc,"Options\u2026");
            require(options!=null,"vanilla options button"); options.onPress(null);
            require(mc.gui.screen() instanceof OptionsScreen,"native options action"); require(UiScope.nativeControls(),"native options styling"); shot="options";
        } else if(step==3) {
            Button sounds=null;
            for(var child:mc.gui.screen().children()) if(child instanceof Button b && b.getMessage().getString().contains("Sounds")) sounds=b;
            require(sounds!=null,"sound options button"); sounds.onPress(null); require(UiScope.nativeControls(),"native sub-options styling");
            require(hasSlider(mc.gui.screen()),"native sliders retained");
            mc.gui.setScreen(new TitleScreen()); Button themes=find(mc,"Themes"); require(themes!=null,"theme button"); themes.onPress(null);
            require(mc.gui.screen() instanceof ThemeGalleryScreen,"gallery action");
            a.menuBackground.set(Appearance.Backdrop.Colosseum); require(a.menuBackground.get()==Appearance.Backdrop.Colosseum,"optional colosseum selection");
            mc.gui.setScreen(new TitleScreen()); shot="optional-colosseum";
        } else if(step>=4 && step<14) {
            a.menuBackground.set(Appearance.Backdrop.Meteor);
            if(step>4) ClientUiAudit.layout(mc);
            var style=Appearance.Style.values()[step-4]; a.select(style);
            meteordevelopment.meteorclient.gui.tabs.Tabs.get().getFirst().openScreen(GuiThemes.get());
            shot="classic-"+style.name().toLowerCase();
        } else if(step==14) {
            ClientUiAudit.layout(mc);
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
            a.gameAnimations.set(true); require(a.animateGame(),"optional gameplay HUD motion enabled");
            mc.getSingleplayerServer().execute(() -> mc.getSingleplayerServer().getCommands().performPrefixedCommand(mc.getSingleplayerServer().createCommandSourceStack(),"setworldspawn 0 76 12"));
            shot="zenith-world-hud";
        } else if(step==23 && mc.player!=null) {
            mc.gui.setScreen(new net.minecraft.client.gui.screens.PauseScreen(true)); shot="game-pause";
        } else if(step==24 && mc.player!=null) {
            Button gameOptions=find(mc,"Options..."); if(gameOptions==null) gameOptions=find(mc,"Options\u2026");
            require(gameOptions!=null,"native pause options button"); gameOptions.onPress(null);
            require(mc.gui.screen() instanceof OptionsScreen,"native pause/options action"); shot="game-options";
        } else {
            require(overlayCaptured,"real branded resource-reload overlay captured");
            a.gameAnimations.set(false); require(!a.animateGame(),"game motion can be disabled");
            a.menuBackground.set(Appearance.Backdrop.Meteor); a.uiAnimations.set(true); a.performance.set(false);
            shot=null;
            try { Files.writeString(out.resolve("RESULT.txt"),"PASS: native menu actions, quick motion presets, native sub-options/sliders, one-state rounded renderer, ten classic palettes, custom accent, editable anchored HUD and toggles, performance mode, optional addon status/settings. Modules: "+Modules.get().getCount()); }
            catch(Exception e) { throw new RuntimeException(e); }
            System.out.println("PURPLE_SMOKE PASS modules="+Modules.get().getCount()); mc.stop();
        }
    }
    private void capture(Minecraft mc,String name) {
        Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),image -> {
            try { image.writeToFile(out.resolve(name+".png")); } catch(Exception e) { throw new RuntimeException(e); } finally { image.close(); }
        });
    }
    private static boolean hasSlider(net.minecraft.client.gui.components.events.GuiEventListener listener) {
        if(listener instanceof net.minecraft.client.gui.components.AbstractSliderButton) return true;
        if(listener instanceof net.minecraft.client.gui.components.events.ContainerEventHandler c)
            for(var child:c.children()) if(hasSlider(child)) return true;
        return false;
    }
    private static Button find(Minecraft mc,String title) {
        for(var child:mc.gui.screen().children()) if(child instanceof Button b && b.getMessage().getString().equals(title)) return b;
        return null;
    }
    private static void require(boolean condition,String label) { if(!condition) throw new AssertionError(label); }
}

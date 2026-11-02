package qa;

import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.*;
import dev.meteorpurple.hud.PurpleHud;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.TitleScreen;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import java.nio.file.*;

public class PurpleSmoke implements ClientModInitializer {
    private int ticks,step=-1;
    private String shot;
    private final Path out=Path.of("../screenshots");
    public void onInitializeClient() {
        try { Files.createDirectories(out); } catch(Exception e) { throw new RuntimeException(e); }
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }
    private void tick(Minecraft mc) {
        if(Appearance.get()==null || mc.gui.screen()==null || mc.gui.overlay()!=null) return;
        if(++ticks<60) return;
        ticks=0;
        if(shot!=null) {
            Path image=out.resolve(shot+".png");
            Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),nativeImage -> {
                try { nativeImage.writeToFile(image); } catch(Exception e) { throw new RuntimeException(e); } finally { nativeImage.close(); }
            });
            System.out.println("PURPLE_SMOKE captured="+shot+" screen="+mc.gui.screen().getClass().getSimpleName());
        }
        Appearance a=Appearance.get(); step++;
        if(step==0) { a.select(Appearance.Style.OS26); mc.gui.setScreen(new TitleScreen()); require(mc.gui.screen() instanceof PurpleTitleScreen,"custom menu route"); shot="home-os26"; }
        else if(step==1) { mc.gui.setScreen(new ThemeGalleryScreen(mc.gui.screen())); shot="theme-gallery"; }
        else if(step>=2 && step<12) {
            var style=Appearance.Style.values()[step-2]; a.select(style);
            mc.gui.setScreen(new PurpleHubScreen(GuiThemes.get()));
            require(Modules.get().getCount()>100,"real Meteor modules present");
            checkSearch((PurpleHubScreen)mc.gui.screen());
            shot="hub-"+style.name().toLowerCase();
        } else if(step==12) { a.select(Appearance.Style.Mocha); PurpleHud.installLayout(); mc.gui.setScreen(new HudEditorScreen(GuiThemes.get())); shot="hud-editor"; }
        else if(step==13) { a.performance.set(true); a.select(Appearance.Style.OS26); require(!a.glassBlurEnabled(),"performance blur bypass"); mc.gui.setScreen(new PurpleHubScreen(GuiThemes.get())); shot="hub-performance"; }
        else if(step==14) { a.performance.set(false); a.applyPending(); a.layout.set(Appearance.Layout.Classic); mc.gui.setScreen(GuiThemes.get().modulesScreen()); require(!(mc.gui.screen() instanceof PurpleHubScreen),"classic route"); shot="classic-os26"; }
        else if(step==15) { a.layout.set(Appearance.Layout.Unified); mc.gui.setScreen(GuiThemes.get().modulesScreen()); require(mc.gui.screen() instanceof PurpleHubScreen,"unified route"); shot="unified-route"; }
        else if(step==16) { a.customMenu.set(false); mc.gui.setScreen(new TitleScreen()); require(mc.gui.screen() instanceof TitleScreen,"vanilla menu fallback"); shot="vanilla-fallback"; }
        else if(step==17) { a.customMenu.set(true); a.select(Appearance.Style.OS26); mc.gui.setScreen(new TitleScreen()); shot="home-final"; }
        else {
            a.customMenu.set(true); a.select(Appearance.Style.OS26); shot=null;
            try { Files.writeString(out.resolve("RESULT.txt"),"PASS: nested bundle loaded; ten palettes and hub screens rendered; menu/classic/unified routes and performance blur bypass checked.\nModules: "+Modules.get().getCount()); } catch(Exception e) { throw new RuntimeException(e); }
            System.out.println("PURPLE_SMOKE PASS modules="+Modules.get().getCount()); mc.stop();
        }
    }
    private static void require(boolean condition,String label) { if(!condition) throw new AssertionError(label); }
    private static void checkSearch(PurpleHubScreen hub) {
        try {
            var field=PurpleHubScreen.class.getDeclaredField("search"); field.setAccessible(true);
            WTextBox search=(WTextBox)field.get(hub);
            var matches=PurpleHubScreen.class.getDeclaredField("filtered"); matches.setAccessible(true);
            search.set("purple-appearance"); search.action.run();
            var list=(java.util.List<?>)matches.get(hub);
            require(list.size()==1 && list.getFirst()==Appearance.get(),"search matches actual module");
            search.set("no-module-can-match-this-query"); search.action.run();
            require(((java.util.List<?>)matches.get(hub)).isEmpty(),"empty search result");
            search.set(""); search.action.run();
        } catch(ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
}

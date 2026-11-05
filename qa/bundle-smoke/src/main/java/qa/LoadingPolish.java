package qa;
import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.PurpleTitleScreen;
import meteordevelopment.meteorclient.gui.GuiThemes;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.TitleScreen;
import java.nio.file.*;

/** Targeted resource-reload capture after a typography-only adjustment; no world/server changes. */
final class LoadingPolish {
    private static int ticks,overlayTicks,phase;
    private static boolean captured;
    private static final Path OUT=Path.of("../screenshots-v15-polish");
    static void start() {
        try { Files.createDirectories(OUT); } catch(Exception e) { throw new RuntimeException(e); }
        ClientTickEvents.END_CLIENT_TICK.register(LoadingPolish::tick);
    }
    private static void tick(Minecraft mc) {
        if(mc.gui.overlay()!=null) {
            if(phase==1 && ++overlayTicks==22) { capture(mc,"loading"); captured=true; }
            return;
        }
        if(Appearance.get()==null || mc.gui.screen()==null || ++ticks<40) return;
        ticks=0;
        if(phase==0) {
            var a=Appearance.get(); a.select(Appearance.Style.Mocha); a.performance.set(false); a.reducedMotion.set(false); a.uiAnimations.set(true);
            mc.gui.setScreen(new TitleScreen()); phase=1; mc.reloadResourcePacks();
        } else if(phase==1) {
            if(!captured) throw new AssertionError("Actual loading overlay capture");
            if(!(mc.gui.screen() instanceof PurpleTitleScreen)) throw new AssertionError("Native menu route");
            capture(mc,"menu"); phase=2; meteordevelopment.meteorclient.gui.tabs.Tabs.get().getFirst().openScreen(GuiThemes.get());
        } else {
            ClientUiAudit.layout(mc); capture(mc,"client");
            try { Files.writeString(OUT.resolve("RESULT.txt"),"PASS: final resource-reload typography, menu route and category bounds"); } catch(Exception e) { throw new RuntimeException(e); }
            System.out.println("PURPLE_POLISH PASS"); mc.stop();
        }
    }
    private static void capture(Minecraft mc,String name) {
        Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),image -> {
            try { image.writeToFile(OUT.resolve(name+".png")); } catch(Exception e) { throw new RuntimeException(e); } finally { image.close(); }
        });
    }
}

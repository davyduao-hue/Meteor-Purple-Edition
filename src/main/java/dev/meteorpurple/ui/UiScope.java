package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;

/** Never repaint another addon's custom screens. Game UI styling is explicitly opt-in. */
public final class UiScope {
    private UiScope() {}
    public static boolean nativeControls() {
        var mc=Minecraft.getInstance(); var screen=mc.gui.screen(); var a=Appearance.get();
        if(screen instanceof PurpleTitleScreen) return true;
        if(a==null || screen==null || !screen.getClass().getName().startsWith("net.minecraft.")) return false;
        if(mc.level!=null ? !a.animateGame():!a.customMenu.get()) return false;
        return screen instanceof PauseScreen || screen instanceof OptionsScreen || screen instanceof OptionsSubScreen
            || screen instanceof SelectWorldScreen || screen instanceof JoinMultiplayerScreen;
    }
}

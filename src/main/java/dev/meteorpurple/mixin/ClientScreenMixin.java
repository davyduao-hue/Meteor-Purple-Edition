package dev.meteorpurple.mixin;

import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.*;
import meteordevelopment.meteorclient.gui.GuiThemes;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class ClientScreenMixin {
    @ModifyVariable(method="setScreen",at=@At("HEAD"),argsOnly=true)
    private Screen purple$routeClientScreen(Screen screen) {
        Appearance a=Appearance.get();
        if (a==null || screen==null) return screen;
        if (screen instanceof TitleScreen && a.customMenu.get()) return new PurpleTitleScreen();
        return screen;
    }
}

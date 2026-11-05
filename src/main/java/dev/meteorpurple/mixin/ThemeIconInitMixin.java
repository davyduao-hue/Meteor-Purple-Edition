package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.ThemeIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=GuiRenderer.class,remap=false)
public abstract class ThemeIconInitMixin {
    @Inject(method="init",at=@At("HEAD"))
    private static void zenith$icons(CallbackInfo ci) { ThemeIcons.init(); }
}

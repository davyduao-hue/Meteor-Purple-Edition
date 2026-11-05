package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.ThemeIcons;
import me.pindour.catppuccin.gui.themes.catppuccin.icons.CatppuccinBuiltinIcons;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=CatppuccinBuiltinIcons.class,remap=false)
public abstract class ThemeIconMixin {
    @Inject(method="texture",at=@At("HEAD"),cancellable=true)
    private void zenith$icon(CallbackInfoReturnable<GuiTexture> ci) {
        if(ThemeIcons.active()) ci.setReturnValue(ThemeIcons.get(ThemeIcons.Icon.valueOf(((Enum<?>)(Object)this).name())));
    }
}

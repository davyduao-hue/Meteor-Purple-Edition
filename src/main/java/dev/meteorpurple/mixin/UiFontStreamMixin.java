package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.UiFont;
import meteordevelopment.meteorclient.utils.render.FontUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.io.InputStream;
@Mixin(value=FontUtils.class,remap=false)
public abstract class UiFontStreamMixin {
    @Inject(method="builtinFontStream",at=@At("HEAD"),cancellable=true)
    private static void zenith$font(String name,CallbackInfoReturnable<InputStream> ci) {
        if(name.equals(UiFont.NAME)) ci.setReturnValue(UiFont.class.getResourceAsStream("/assets/meteor-purple-edition/font/fredoka.ttf"));
    }
}

package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.PurpleTitleScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(TitleScreen.class)
public abstract class MenuSplashMixin {
    @Redirect(method="extractRenderState",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/components/SplashRenderer;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;ILnet/minecraft/client/gui/Font;F)V"))
    private void zenith$splash(SplashRenderer splash,GuiGraphicsExtractor g,int w,Font font,float alpha) {
        if(!((Object)this instanceof PurpleTitleScreen)) splash.extractRenderState(g,w,font,alpha);
    }
}

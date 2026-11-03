package dev.meteorpurple.mixin;

import dev.meteorpurple.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Preserve the vanilla button instances, handlers, permissions and narration. */
@Mixin(AbstractButton.class)
public abstract class MenuButtonMixin {
    @Unique private final ButtonMotion purple$motion=new ButtonMotion();
    @Inject(method="extractWidgetRenderState",at=@At("HEAD"),cancellable=true)
    private void purple$paintMenuButton(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta,CallbackInfo ci) {
        AbstractButton b=(AbstractButton)(Object)this;
        if(Minecraft.getInstance().gui.screen() instanceof PurpleTitleScreen && !(b instanceof PurpleButton)
            && b.getWidth()>=80 && b.getHeight()>=18) {
            ButtonPaint.draw(g,b,Canvas.palette(),purple$motion);
            ci.cancel();
        }
    }
}

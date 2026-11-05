package dev.meteorpurple.mixin;

import dev.meteorpurple.ui.*;
import net.minecraft.client.Minecraft;
import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Preserve the vanilla button instances, handlers, permissions and narration. */
@Mixin(AbstractButton.class)
public abstract class MenuButtonMixin {
    @Shadow protected abstract void extractContents(GuiGraphicsExtractor g,int x,int y,float delta);
    @Unique private final ButtonMotion purple$motion=new ButtonMotion();
    @Inject(method="onClick",at=@At("HEAD"))
    private void zenith$press(MouseButtonEvent event,boolean doubleClick,CallbackInfo ci) { purple$motion.press(); }
    @Redirect(method="extractWidgetRenderState",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/components/AbstractButton;extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
    private void purple$paintMenuButton(AbstractButton b,GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
        if(UiScope.nativeControls() && !(b instanceof PurpleButton)
            && b.getWidth()>=80 && b.getHeight()>=18) {
            ButtonPaint.draw(g,b,Canvas.palette(),purple$motion);
        } else extractContents(g,mouseX,mouseY,delta);
    }
    @Inject(method="extractDefaultSprite",at=@At("HEAD"),cancellable=true)
    private void zenith$smallControls(GuiGraphicsExtractor g,CallbackInfo ci) {
        var b=(AbstractButton)(Object)this;
        if(!(net.minecraft.client.Minecraft.getInstance().gui.screen() instanceof PurpleTitleScreen) || b.getWidth()>28 || b.getHeight()>28) return;
        Palette p=Canvas.palette();
        purple$motion.update(b.isHoveredOrFocused(),MotionPrefs.ui(),170,System.nanoTime());
        int x=b.getX(),y=b.getY(),w=b.getWidth(),h=b.getHeight();
        Canvas.rounded(g,x,y,w,h,5,Palette.argb(p.accent,Math.round(65+purple$motion.amount()*140)));
        Canvas.rounded(g,x+1,y+1,w-2,h-2,4,Palette.argb(p.surface0,255));
        ci.cancel();
    }
}

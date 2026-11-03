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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Preserve the vanilla button instances, handlers, permissions and narration. */
@Mixin(AbstractButton.class)
public abstract class MenuButtonMixin {
    @Unique private final ButtonMotion purple$motion=new ButtonMotion();
    @Inject(method="onClick",at=@At("HEAD"))
    private void zenith$press(MouseButtonEvent event,boolean doubleClick,CallbackInfo ci) { purple$motion.press(); }
    @Inject(method="extractWidgetRenderState",at=@At("HEAD"),cancellable=true)
    private void purple$paintMenuButton(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta,CallbackInfo ci) {
        AbstractButton b=(AbstractButton)(Object)this;
        var mc=Minecraft.getInstance(); var a=Appearance.get();
        boolean game=a!=null && a.animateGame() && mc.level!=null && (mc.gui.screen() instanceof PauseScreen || mc.gui.screen() instanceof OptionsScreen);
        if((mc.gui.screen() instanceof PurpleTitleScreen || game) && !(b instanceof PurpleButton)
            && b.getWidth()>=80 && b.getHeight()>=18) {
            ButtonPaint.draw(g,b,Canvas.palette(),purple$motion);
            ci.cancel();
        }
    }
}

package dev.meteorpurple.mixin;

import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Exact native values and input handlers; only the slider presentation is replaced. */
@Mixin(AbstractSliderButton.class)
public abstract class MenuSliderMixin {
    @Shadow protected double value;
    @Shadow protected abstract void handleCursor(GuiGraphicsExtractor g);
    @Unique private final ButtonMotion zenith$motion=new ButtonMotion();
    @Inject(method="extractWidgetRenderState",at=@At("HEAD"),cancellable=true)
    private void zenith$slider(GuiGraphicsExtractor g,int mx,int my,float delta,CallbackInfo ci) {
        if(!UiScope.nativeControls()) return;
        var b=(AbstractSliderButton)(Object)this; var mc=Minecraft.getInstance(); var a=Appearance.get();
        boolean animate=a!=null && (mc.level!=null ? a.animateGame():a.animate());
        zenith$motion.update(b.isHoveredOrFocused(),animate,a==null ? 170:a.animationMs(),System.nanoTime());
        Palette p=Canvas.palette(); int x=b.getX(),y=b.getY(),w=b.getWidth(),h=b.getHeight();
        Canvas.rounded(g,x,y,w,h,6,Palette.argb(p.surface1,255));
        int left=x+7,right=x+w-7,cy=y+h-4;
        Canvas.rounded(g,left,cy,right-left,2,1,Palette.argb(p.accent,45));
        int thumb=left+(int)Math.round((right-left)*Math.max(0,Math.min(1,value)));
        Canvas.rounded(g,left,cy,thumb-left,2,1,Palette.argb(p.accent,b.active ? 220:80));
        int size=animate ? 4+Math.round(zenith$motion.amount()*2):4;
        Canvas.rounded(g,thumb-size/2,cy-size/2+1,size,size,size/2,Palette.argb(p.accent,b.active ? 255:90));
        var label=zenith$motion.label(b.getMessage());
        Canvas.fitted(g,label,zenith$motion.labelWidth(mc.font),x+w/2,y+3,w-12,Palette.argb(p.text,b.active ? 255:100));
        handleCursor(g); ci.cancel();
    }
}

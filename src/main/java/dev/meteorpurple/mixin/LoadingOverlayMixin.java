package dev.meteorpurple.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.meteorpurple.ui.LoadingBrand;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.IntSupplier;

/** Changes presentation only; native reload completion, fades and errors still run. */
@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    @Shadow private float currentProgress;
    @Unique private boolean zenith$drawn;
    @Inject(method="extractRenderState",at=@At("HEAD"))
    private void zenith$begin(GuiGraphicsExtractor g,int x,int y,float delta,CallbackInfo ci) { zenith$drawn=false; }
    @Redirect(method="extractRenderState",at=@At(value="INVOKE",target="Ljava/util/function/IntSupplier;getAsInt()I"))
    private int zenith$background(IntSupplier original) { return 0xff10131f; }
    @Redirect(method="extractRenderState",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"))
    private void zenith$brand(GuiGraphicsExtractor g,RenderPipeline pipeline,Identifier id,int x,int y,float u,float v,int w,int h,int rw,int rh,int tw,int th,int color) {
        if(!zenith$drawn) { LoadingBrand.draw(g,color>>>24); zenith$drawn=true; }
    }
    @Inject(method="extractProgressBar",at=@At("HEAD"),cancellable=true)
    private void zenith$progress(GuiGraphicsExtractor g,int left,int top,int right,int bottom,float alpha,CallbackInfo ci) {
        int a=Math.round(255*Math.max(0,Math.min(1,alpha)));
        g.fill(left,top,right,bottom,(a<<24)|0x25263a);
        g.fill(left,top,left+Math.round((right-left)*Math.max(0,Math.min(1,currentProgress))),bottom,(a<<24)|0xb49af0);
        ci.cancel();
    }
}

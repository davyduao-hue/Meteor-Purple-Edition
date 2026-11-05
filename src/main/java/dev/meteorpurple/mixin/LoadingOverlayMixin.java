package dev.meteorpurple.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.meteorpurple.ui.*;
import net.minecraft.client.renderer.texture.TextureManager;
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
    @Unique private final LoadingMotion zenith$motion=new LoadingMotion();
    @Inject(method="registerTextures",at=@At("TAIL"))
    private static void zenith$typography(TextureManager manager,CallbackInfo ci) { BrandAtlas.register(manager); }
    @Inject(method="extractRenderState",at=@At("HEAD"))
    private void zenith$begin(GuiGraphicsExtractor g,int x,int y,float delta,CallbackInfo ci) { zenith$drawn=false; }
    @Redirect(method="extractRenderState",at=@At(value="INVOKE",target="Ljava/util/function/IntSupplier;getAsInt()I"))
    private int zenith$background(IntSupplier original) { return Palette.argb(Canvas.palette().mantle,255); }
    @Redirect(method="extractRenderState",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"))
    private void zenith$brand(GuiGraphicsExtractor g,RenderPipeline pipeline,Identifier id,int x,int y,float u,float v,int w,int h,int rw,int rh,int tw,int th,int color) {
        if(!zenith$drawn) { LoadingBrand.draw(g,currentProgress,color>>>24,zenith$motion); zenith$drawn=true; }
    }
    @Inject(method="extractProgressBar",at=@At("HEAD"),cancellable=true)
    private void zenith$progress(GuiGraphicsExtractor g,int left,int top,int right,int bottom,float alpha,CallbackInfo ci) {
        ci.cancel();
    }
}


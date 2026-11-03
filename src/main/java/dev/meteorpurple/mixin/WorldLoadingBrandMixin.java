package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LevelLoadingScreen.class)
public abstract class WorldLoadingBrandMixin {
    @Shadow private float smoothedProgress;
    @Unique private final LoadingMotion zenith$motion=new LoadingMotion();
    @Inject(method="extractBackground",at=@At("HEAD"),cancellable=true)
    private void zenith$background(GuiGraphicsExtractor g,int x,int y,float delta,CallbackInfo ci) {
        g.fillGradient(0,0,g.guiWidth(),g.guiHeight(),0xff10131f,0xff19162b);
        ci.cancel();
    }
    @Inject(method="extractRenderState",at=@At("TAIL"))
    private void zenith$caption(GuiGraphicsExtractor g,int x,int y,float delta,CallbackInfo ci) {
        g.nextStratum(); g.fillGradient(0,0,g.guiWidth(),g.guiHeight(),0xff10131f,0xff19162b);
        LoadingBrand.draw(g,smoothedProgress,255,zenith$motion);
    }
}

package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.api.animation.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=Animation.class,remap=false)
public abstract class ClassicAnimationMixin {
    @Shadow private Direction direction;
    @Inject(method="getProgress",at=@At("HEAD"),cancellable=true)
    private void zenith$still(CallbackInfoReturnable<Double> ci) { if(ClassicPaint.enabled() && !MotionPrefs.ui()) ci.setReturnValue(direction.isForwards() ? 1d:0d); }
    @Inject(method="isRunning",at=@At("HEAD"),cancellable=true)
    private void zenith$running(CallbackInfoReturnable<Boolean> ci) { if(ClassicPaint.enabled() && !MotionPrefs.ui()) ci.setReturnValue(false); }
    @Inject(method="isFinished",at=@At("HEAD"),cancellable=true)
    private void zenith$finished(CallbackInfoReturnable<Boolean> ci) { if(ClassicPaint.enabled() && !MotionPrefs.ui()) ci.setReturnValue(true); }
}

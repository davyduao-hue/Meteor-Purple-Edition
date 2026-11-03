package dev.meteorpurple.mixin;

import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.PurpleTitleScreen;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=TitleScreenCredits.class,remap=false)
public abstract class MenuCreditsMixin {
    @Unique private static boolean purple$hide() {
        Appearance a=Appearance.get();
        return a!=null && Minecraft.getInstance().gui.screen() instanceof PurpleTitleScreen && !a.menuCredits.get();
    }
    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private static void purple$credits(GuiGraphicsExtractor g,CallbackInfo ci) { if(purple$hide()) ci.cancel(); }
    @Inject(method="onClicked",at=@At("HEAD"),cancellable=true)
    private static void purple$clicks(double x,double y,CallbackInfoReturnable<Boolean> cir) { if(purple$hide()) cir.setReturnValue(false); }
}

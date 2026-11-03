package dev.meteorpurple.mixin;

import dev.meteorpurple.ui.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Select a separate cubemap only for our main menu; vanilla resources remain intact. */
@Mixin(CubeMap.class)
public abstract class ColosseumPanoramaMixin {
    @Inject(method="registerTextures",at=@At("TAIL"))
    private void zenith$register(TextureManager manager,CallbackInfo ci) {
        manager.register(ColosseumPanorama.TEXTURE,new CubeMapTexture(ColosseumPanorama.TEXTURE));
    }
    @ModifyVariable(method="render",at=@At("HEAD"),argsOnly=true,ordinal=1)
    private float zenith$initialView(float yaw) {
        return Minecraft.getInstance().gui.screen() instanceof PurpleTitleScreen ? yaw+180:yaw;
    }
    @ModifyArg(method="render",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/texture/TextureManager;getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;"),index=0)
    private Identifier zenith$select(Identifier original) {
        return Minecraft.getInstance().gui.screen() instanceof PurpleTitleScreen ? ColosseumPanorama.TEXTURE:original;
    }
}

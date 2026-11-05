package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.ThemeIcons;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

/** Our atlases already carry their theme colors; preserve caller opacity, without multiplying by dark text. */
@Mixin(value=GuiRenderer.class,remap=false)
public abstract class ThemeIconTintMixin {
    @Unique private final Color zenith$white=new Color(255,255,255,255);
    @ModifyVariable(method="quad(DDDDLmeteordevelopment/meteorclient/gui/renderer/packer/GuiTexture;Lmeteordevelopment/meteorclient/utils/render/color/Color;)V",at=@At("HEAD"),argsOnly=true)
    private Color zenith$iconTint(Color original,double x,double y,double w,double h,GuiTexture texture,Color ignored) {
        if(!ThemeIcons.owns(texture)) return original;
        zenith$white.set(255,255,255,original.a); return zenith$white;
    }
    @ModifyVariable(method="rotatedQuad(DDDDDLmeteordevelopment/meteorclient/gui/renderer/packer/GuiTexture;Lmeteordevelopment/meteorclient/utils/render/color/Color;)V",at=@At("HEAD"),argsOnly=true)
    private Color zenith$rotatedTint(Color original,double x,double y,double w,double h,double angle,GuiTexture texture,Color ignored) {
        if(!ThemeIcons.owns(texture)) return original;
        zenith$white.set(255,255,255,original.a); return zenith$white;
    }
}

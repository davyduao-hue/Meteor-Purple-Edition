package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinTextBox;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=WCatppuccinTextBox.class,remap=false)
public abstract class ClassicInputMixin {
    @Unique private final ClassicMotion zenith$motion=new ClassicMotion();
    @Unique private final Color zenith$edge=new Color();
    @Inject(method="onRender",at=@At("TAIL"))
    private void zenith$focus(GuiRenderer r,double mx,double my,double delta,CallbackInfo ci) {
        if(!ClassicPaint.enabled()) return;
        var w=(WWidget)(Object)this; var p=Canvas.palette();
        double t=zenith$motion.update(w.mouseOver,w.focused,delta),on=zenith$motion.active.value();
        zenith$edge.set(p.accent.r,p.accent.g,p.accent.b,(int)(25+t*50+on*140));
        ClassicPaint.panel(w.x,w.y,w.width,w.height,w.theme.scale(ThemeDesign.current().controlRadius()),ClassicPaint.CLEAR,zenith$edge);
    }
}

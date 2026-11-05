package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="me.pindour.catppuccin.gui.themes.catppuccin.widgets.container.WCatppuccinWindow$WCatppuccinHeader",remap=false)
public abstract class ClassicHeaderMixin {
    @Inject(method="onRender",at=@At("TAIL"))
    private void zenith$header(GuiRenderer r,double mx,double my,double delta,CallbackInfo ci) {
        if(!ClassicPaint.enabled()) return;
        var w=(WWidget)(Object)this; var p=Canvas.palette();
        ClassicPaint.rect(w.x+2,w.y+2,w.width-4,w.height-2,w.theme.scale(ThemeDesign.current().windowRadius()),ClassicPaint.header());
        ClassicPaint.rect(w.x+10,w.y+w.height-1,w.width-20,1,.5,ClassicPaint.edge());
    }
}

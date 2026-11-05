package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.WCatppuccinTopBar;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=WCatppuccinTopBar.class,remap=false)
public abstract class ClassicTopBarMixin {
    @Inject(method="onRender",at=@At("HEAD"),cancellable=true)
    private void zenith$nav(GuiRenderer r,double mx,double my,double delta,CallbackInfo ci) {
        if(!ClassicPaint.enabled()) return;
        var w=(WWidget)(Object)this; var p=Canvas.palette();
        ClassicPaint.panel(w.x,w.y,w.width,w.height,w.theme.scale(ThemeDesign.current().windowRadius()),p.base,ClassicPaint.edge()); ci.cancel();
    }
}

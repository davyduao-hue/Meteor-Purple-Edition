package dev.meteorpurple.mixin;

import dev.meteorpurple.ui.ClassicPaint;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static meteordevelopment.meteorclient.utils.Utils.*;

/** Wrap actual widths and budget scrollable body heights. WWindow still restores each saved drag. */
@Mixin(targets="me.pindour.catppuccin.gui.screens.CatppuccinModulesScreen$WCategoryController",remap=false)
public abstract class ClassicCategoryLayoutMixin extends WContainer {
    @Inject(method="onCalculateWidgetPositions",at=@At("HEAD"),cancellable=true)
    private void zenith$layout(CallbackInfo ci) {
        if(!ClassicPaint.enabled() || cells.isEmpty()) return;
        double gap=theme.scale(10),margin=theme.scale(16),available=getWindowWidth()-margin*2;
        int rows=1; double used=0;
        for(var cell:cells) {
            double w=cell.widget().width;
            if(used>0 && used+gap+w>available) { rows++; used=0; }
            used+=w+(used==0 ? 0:gap);
        }
        double top=theme.scale(84),rowHeight=Math.max(theme.scale(90),(getWindowHeight()-top-theme.scale(48))/rows);
        double xx=margin,yy=top;
        for(var cell:cells) {
            var widget=cell.widget();
            if(widget instanceof WWindow w) {
                w.view.maxHeight=Math.max(theme.scale(25),rowHeight-theme.scale(58));
                w.view.hasScrollBar=true; w.calculateSize();
            }
            if(xx>margin && xx+widget.width>getWindowWidth()-margin) { xx=margin; yy+=rowHeight+gap; }
            cell.x=xx; cell.y=Math.min(yy,getWindowHeight()-widget.height-theme.scale(12));
            cell.width=widget.width; cell.height=widget.height; cell.alignWidget();
            xx+=widget.width+gap;
        }
        ci.cancel();
    }
}

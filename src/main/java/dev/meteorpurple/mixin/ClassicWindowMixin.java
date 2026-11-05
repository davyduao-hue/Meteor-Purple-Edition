package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.api.animation.Animation;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinWidget;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.container.WCatppuccinWindow;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=WCatppuccinWindow.class,remap=false)
public abstract class ClassicWindowMixin extends WWindow implements CatppuccinWidget {
    @Shadow private Animation animation;
    @Unique private final meteordevelopment.meteorclient.utils.render.color.Color zenith$surface=new meteordevelopment.meteorclient.utils.render.color.Color();
    @Unique private final Spring zenith$entry=new Spring(0);
    @Unique private float zenith$offset;
    @Unique private long zenith$opened;
    protected ClassicWindowMixin(WWidget icon,String title) { super(icon,title); }
    @Inject(method="render",at=@At("HEAD"))
    private void zenith$entrance(meteordevelopment.meteorclient.gui.renderer.GuiRenderer r,double mx,double my,double delta,org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> ci) {
        zenith$offset=0;
        if(!ClassicPaint.enabled()) return;
        if(!MotionPrefs.ui()) { zenith$entry.reset(1); return; }
        if(zenith$opened==0) zenith$opened=System.nanoTime();
        double delay=id==null ? 0:Math.floorMod(id.hashCode(),4)*.025;
        if((System.nanoTime()-zenith$opened)/1_000_000_000d>=delay) zenith$entry.update(1,(float)delta,12);
        zenith$offset=Math.round((1-zenith$entry.value())*10*MotionPrefs.energy());
        if(zenith$offset!=0) move(0,zenith$offset);
    }
    @Inject(method="render",at=@At("RETURN"))
    private void zenith$restore(meteordevelopment.meteorclient.gui.renderer.GuiRenderer r,double mx,double my,double delta,org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> ci) {
        if(zenith$offset!=0) { move(0,-zenith$offset); zenith$offset=0; }
    }
    @Inject(method="renderBackground",at=@At("HEAD"),cancellable=true)
    private void zenith$window(CallbackInfo ci) {
        if(!ClassicPaint.enabled()) return;
        Palette p=Canvas.palette(); double body=Math.max(0,(height-header.height)*animation.getProgress()),h=header.height+body;
        double radius=theme().scale(ThemeDesign.current().windowRadius());
        if(!MotionPrefs.lowCost()) ClassicPaint.rect(x+1,y+theme.scale(4),width,h,radius,ClassicPaint.shadow());
        zenith$surface.set(p.mantle.r,p.mantle.g,p.mantle.b,(int)(255*theme().windowOpacity()));
        ClassicPaint.panel(x,y,width,h,radius,zenith$surface,ClassicPaint.edge());
        // A colored header band and a single hairline provide depth without a blur pass.
        ClassicPaint.rect(x+1,y+1,width-2,header.height-1,radius,ClassicPaint.header());
        ci.cancel();
    }
}

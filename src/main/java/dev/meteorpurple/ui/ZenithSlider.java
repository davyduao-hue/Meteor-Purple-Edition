package dev.meteorpurple.ui;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinSlider;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;

/** The painted position uses the exact native value. Only thumb size has a spring. */
public final class ZenithSlider extends WCatppuccinSlider {
    private final ClassicMotion motion=new ClassicMotion();
    public ZenithSlider(double value,double min,double max) { super(value,min,max); }
    @Override protected void onRender(GuiRenderer r,double mx,double my,double delta) {
        Palette p=Canvas.palette(); double s=theme.scale(1),t=motion.update(handleMouseOver || mouseOver,dragging,delta);
        double cy=y+height/2,cx=x+valueWidth()+handleSize()/2;
        double track=theme.scale(5);
        ClassicPaint.rect(x+handleSize()/2,cy-track/2,width-handleSize(),track,track/2,p.surface2);
        ClassicPaint.rect(x+handleSize()/2,cy-track/2,valueWidth(),track,track/2,p.accent);
        double size=theme.scale(11)+t*s*2+motion.active.value()*s*2;
        if(!MotionPrefs.lowCost()) ClassicPaint.rect(cx-size*.7,cy-size*.7,size*1.4,size*1.4,size,ClassicPaint.faint());
        ClassicPaint.rect(cx-size/2,cy-size/2,size,size,size/2,p.accent);
        ClassicPaint.rect(cx-s,cy-s,2*s,2*s,s,p.base);
    }
}

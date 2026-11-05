package dev.meteorpurple.ui;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.WCatppuccinCheckbox;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public final class ZenithCheckbox extends WCatppuccinCheckbox {
    private final ClassicMotion motion=new ClassicMotion();
    private final Color track=new Color();
    public ZenithCheckbox(boolean checked) { super(checked); motion.active.reset(checked ? 1:0); }
    @Override protected void onCalculateSize() { width=theme.scale(34); height=theme.scale(20); }
    @Override protected void onPressed(int button) { motion.click(); super.onPressed(button); }
    @Override protected void onRender(GuiRenderer r,double mx,double my,double delta) {
        Palette p=Canvas.palette(); double t=motion.update(mouseOver || focused,checked,delta);
        double on=Math.max(0,Math.min(1,motion.active.value())),s=theme.scale(1);
        ClassicPaint.mix(track,p.surface2,p.accent,on);
        ClassicPaint.panel(x,y,width,height,height/2,track,ClassicPaint.edge());
        double knob=height-6*s,stretch=motion.press.value()*2*s;
        double kx=x+3*s+(width-knob-6*s)*on;
        ClassicPaint.rect(kx-stretch/2,y+3*s,knob+stretch,knob,knob/2,checked ? p.base:p.text);
        if(t>.01 && !MotionPrefs.lowCost()) ClassicPaint.rect(x+width*.25,y+height+2*s,width*.5*t,s,s,ClassicPaint.faint());
    }
}

package dev.meteorpurple.ui;
import net.minecraft.network.chat.Component;
public final class ButtonMotion {
    private long previous;
    private boolean hovered;
    private float age,pressAge=2;
    private final Spring hover=new Spring(0),press=new Spring(0);
    private Component original,label;
    private net.minecraft.client.gui.Font measuredFont;
    private int measuredWidth;
    public void update(boolean target,boolean animate,int durationMs,long now) {
        float dt=previous==0 ? 1/60f:Math.min(.1f,Math.max(0,(now-previous)/1_000_000_000f)); previous=now;
        if(target && !hovered) age=0; else if(target) age+=dt;
        hovered=target;
        pressAge+=dt;
        if(!animate) { hover.reset(target ? 1:0); press.reset(0); age=1; pressAge=2; return; }
        hover.update(target ? 1:0,dt,Math.max(8,3500f/Math.max(1,durationMs))); press.update(0,dt,25);
    }
    public void press() { press.reset(1); pressAge=0; }
    public float amount() { return Math.max(0,Math.min(1,hover.value())); }
    public float pop() { return hover.value(); }
    public float pressAmount() { return Math.max(0,press.value()); }
    public float sweep() { return hovered && age<.48f ? age/.48f:-1; }
    public float burst() { return hovered && age<.42f ? age/.42f:-1; }
    public float ripple() { return pressAge<.5f ? pressAge/.5f:-1; }
    public int labelWidth(net.minecraft.client.gui.Font font) {
        if(measuredFont!=font) { measuredWidth=font.width(label); measuredFont=font; }
        return measuredWidth;
    }
    public Component label(Component source) {
        if(original!=source) { original=source; label=source.copy().withStyle(s -> s.withFont(Canvas.FONT)); measuredFont=null; }
        return label;
    }
}

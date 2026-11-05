package dev.meteorpurple.ui;

/** Widget-local state. Resting springs return immediately and do no integration work. */
public final class ClassicMotion {
    public final Spring hover=new Spring(0),active=new Spring(0),press=new Spring(0);
    public float update(boolean over,boolean on,double delta) {
        boolean animate=MotionPrefs.ui();
        if(!animate) { hover.reset(over ? 1:0); active.reset(on ? 1:0); press.reset(0); return over ? 1:0; }
        float dt=(float)Math.max(0,Math.min(.1,delta));
        hover.update(over ? 1:0,dt,19); active.update(on ? 1:0,dt,17); press.update(0,dt,24);
        return Math.max(0,Math.min(1,hover.value()));
    }
    public void click() { if(MotionPrefs.ui()) press.reset(1); }
}

package dev.meteorpurple.ui;

/** One short sweep per hover/focus entry; no looping animation while idle. */
public final class ButtonMotion {
    private long previous;
    private boolean hovered;
    private float amount,age;
    public void update(boolean target,boolean animate,int durationMs,long now) {
        float dt=previous==0 ? 0:Math.min(.1f,Math.max(0,(now-previous)/1_000_000_000f));
        previous=now;
        if(target && !hovered) age=0;
        else if(target) age+=dt;
        hovered=target;
        if(!animate) { amount=target ? 1:0; age=1; return; }
        float step=dt*1000/Math.max(1,durationMs);
        amount=target ? Math.min(1,amount+step):Math.max(0,amount-step);
    }
    public float amount() { return amount*amount*(3-2*amount); }
    public float sweep() { return hovered && age<.45f ? age/.45f:-1; }
}

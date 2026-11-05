package dev.meteorpurple.ui;
/** Damped motion with bounded integration steps, including after long frame stalls. */
public final class Spring {
    private float value,velocity;
    public Spring(float initial) { value=initial; }
    public void reset(float v) { value=v; velocity=0; }
    public void kick(float impulse) { velocity+=impulse; }
    public float value() { return value; }
    public float update(float target,float dt,float frequency) {
        if(value==target && velocity==0) return value;
        dt=Math.max(0,Math.min(.1f,dt));
        int steps=Math.max(1,(int)Math.ceil(dt/.008f)); float h=dt/steps;
        for(int i=0;i<steps;i++) {
            velocity+=((target-value)*frequency*frequency-1.4f*frequency*velocity)*h;
            value+=velocity*h;
        }
        if(Math.abs(value-target)<.0002f && Math.abs(velocity)<.002f) reset(target);
        return value;
    }
}

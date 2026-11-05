package dev.meteorpurple.ui;

/** Reusable loading springs and twelve fixed spark slots, driven by native progress. */
public final class LoadingMotion {
    public static final int MAX_SPARKS=12;
    public final Spring enter=new Spring(0),progress=new Spring(0),impact=new Spring(0);
    public final float[] px=new float[MAX_SPARKS],py=new float[MAX_SPARKS],vx=new float[MAX_SPARKS],vy=new float[MAX_SPARKS],life=new float[MAX_SPARKS];
    private long previous,started;
    private float lastProgress,burstCooldown;
    public float time;
    public void update(float target,boolean animate,long now) {
        target=Math.max(0,Math.min(1,target));
        if(started==0) started=now;
        time=(now-started)/1_000_000_000f;
        float dt=previous==0 ? 1/60f:Math.min(.1f,Math.max(0,(now-previous)/1_000_000_000f)); previous=now;
        if(!animate) { enter.reset(1); progress.reset(target); impact.reset(0); java.util.Arrays.fill(life,0); lastProgress=target; return; }
        enter.update(1,dt,11); progress.update(target,dt,15); impact.update(0,dt,18); burstCooldown-=dt;
        if(target-lastProgress>.012f && burstCooldown<=0) {
            impact.kick(4); burstCooldown=.08f;
            for(int i=0;i<MAX_SPARKS;i++) { px[i]=py[i]=0; vx[i]=(i-5.5f)*7; vy[i]=-18-(i%4)*9; life[i]=.6f; }
        }
        lastProgress=target;
        for(int i=0;i<MAX_SPARKS;i++) if(life[i]>0) { life[i]=Math.max(0,life[i]-dt); px[i]+=vx[i]*dt; py[i]+=vy[i]*dt; vy[i]+=65*dt; }
    }
}

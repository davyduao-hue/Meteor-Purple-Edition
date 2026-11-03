package dev.meteorpurple.ui;
public final class LoadingMotion {
    public final Spring enter=new Spring(0),progress=new Spring(0),impact=new Spring(0);
    public final float[] px=new float[8],py=new float[8],vx=new float[8],vy=new float[8],life=new float[8];
    private long previous,started;
    private float lastProgress;
    public float time;
    public void update(float target,boolean animate,long now) {
        if(started==0) started=now;
        time=(now-started)/1_000_000_000f;
        float dt=previous==0 ? 1/60f:Math.min(.1f,Math.max(0,(now-previous)/1_000_000_000f)); previous=now;
        if(!animate) { enter.reset(1); progress.reset(target); impact.reset(0); java.util.Arrays.fill(life,0); return; }
        enter.update(1,dt,12); progress.update(target,dt,15); impact.update(0,dt,18);
        if(target-lastProgress>.015f) {
            impact.kick(3);
            for(int i=0;i<6;i++) { px[i]=py[i]=0; vx[i]=(i-2.5f)*11; vy[i]=-18-(i%3)*7; life[i]=.55f; }
        }
        lastProgress=target;
        for(int i=0;i<6;i++) if(life[i]>0) { life[i]-=dt; px[i]+=vx[i]*dt; py[i]+=vy[i]*dt; vy[i]+=55*dt; }
    }
}

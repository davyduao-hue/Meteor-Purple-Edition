package dev.meteorpurple.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Fixed ambient mote count, deterministic paths, no growing emitter or random allocations. */
public final class MenuAtmosphere {
    public static final int MAX_MOTES=24;
    public void draw(GuiGraphicsExtractor g,int width,int height,float heroY,float time,float followX,float followY) {
        Palette p=Canvas.palette(); boolean animate=MotionPrefs.ui(),low=MotionPrefs.lowCost();
        g.fillGradient(0,0,width,height,Palette.argb(p.mantle,255),Palette.argb(p.base,255));
        if(low) return;
        float strength=MotionPrefs.energy();
        float drift=animate ? (float)Math.sin(time*.24f)*12*strength:0;
        RadialPaint.glow(g,width*.5f+followX,heroY+followY,width*.46f,170,Palette.argb(p.accent,p.light ? 32:36));
        RadialPaint.glow(g,width*.15f+drift,height*.63f,150,130,Palette.argb(p.accent,p.light ? 13:12));
        RadialPaint.glow(g,width*.89f-drift,height*.25f,180,130,Palette.argb(p.accent,p.light ? 10:12));
        // A perspective halo gives the floating logo a stage without a giant background image.
        RadialPaint.ring(g,width*.5f,heroY+44,88,17,Palette.argb(p.accent,p.light ? 45:40));
        RadialPaint.glow(g,width*.5f,heroY+44,78,13,Palette.argb(p.accent,40));
        if(!animate || !MotionPrefs.particles()) return;
        int count=strength<.7f ? 8:strength>1.2f ? MAX_MOTES:16;
        for(int i=0;i<count;i++) {
            float phase=i*2.399963f;
            float nx=(i*.618034f)%1,ny=(i*.381966f)%1;
            float x=nx*width+(float)Math.sin(time*(.12f+i*.004f)+phase)*12*strength+followX*(i%3+1);
            float y=((ny*height-time*(1+i%4)*strength)%height+height)%height;
            int alpha=(int)(20+27*(.5+.5*Math.sin(time*1.2+phase)));
            int size=i%5==0 ? 2:1;
            g.fill((int)x,(int)y,(int)x+size,(int)y+size,Palette.argb(p.accent,alpha));
            if(i%5==0) { g.fill((int)x-2,(int)y,(int)x+size+2,(int)y+1,Palette.argb(p.accent,alpha/3)); }
        }
    }
}

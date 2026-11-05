package dev.meteorpurple.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Expressive presentation of real loading progress. No minimum duration or completion delay. */
public final class LoadingBrand {
    private LoadingBrand() {}
    public static void draw(GuiGraphicsExtractor g,float raw,int alpha,LoadingMotion motion) {
        if(alpha<=0) return;
        raw=Math.max(0,Math.min(1,raw)); boolean animate=MotionPrefs.ui(),low=MotionPrefs.lowCost();
        motion.update(raw,animate,System.nanoTime()); float energy=MotionPrefs.energy();
        float unit=Math.min(1,Math.min(g.guiWidth()/560f,g.guiHeight()/340f));
        int cx=g.guiWidth()/2,cy=Math.round(g.guiHeight()*.43f);
        float enter=motion.enter.value(),lift=(1-enter)*18*unit;
        float bob=animate ? (float)Math.sin(motion.time*2.1)*2.3f*energy:0;
        Palette p=Canvas.palette();
        if(!low) {
            RadialPaint.glow(g,cx,cy-55*unit,230*unit,155*unit,Palette.argb(p.accent,Math.round(alpha*.18f)));
            RadialPaint.ring(g,cx,cy-37*unit,76*unit,15*unit,Palette.argb(p.accent,Math.round(alpha*.12f)));
            if(animate && MotionPrefs.particles()) for(int i=0;i<3;i++) {
                float angle=motion.time*(.7f+i*.12f)+i*2.094f;
                int x=Math.round(cx+(float)Math.cos(angle)*80*unit),y=Math.round(cy-50*unit+(float)Math.sin(angle)*38*unit);
                g.fill(x,y,x+2,y+2,Palette.argb(p.accent,Math.round(alpha*.55f)));
            }
        }
        MeteorMark.drawAssembled(g,cx,cy-76*unit+lift+bob,94*unit*(.85f+.15f*enter),alpha,
            animate ? (float)Math.sin(motion.time*1.2f)*.024f*energy:0,animate ? Math.max(0,1-motion.time/.8f):0);
        g.pose().pushMatrix(); g.pose().translate(0,lift*.4f);
        BrandAtlas.line(g,0,cx,cy+Math.round(3*unit),Math.round(310*unit),Palette.argb(p.text,alpha));
        BrandAtlas.line(g,1,cx,cy+Math.round(61*unit),Math.round(270*unit),Palette.argb(p.muted,Math.round(alpha*.85f)));
        g.pose().popMatrix();
        int w=Math.round(Math.min(298*unit,g.guiWidth()-40)),h=Math.max(8,Math.round(12*unit)),x=cx-w/2,y=cy+Math.round(105*unit);
        float impact=animate ? Math.min(.15f,motion.impact.value()*.22f)*energy:0;
        g.pose().pushMatrix(); g.pose().translate(cx,y+h/2f); g.pose().scale(1,1+impact); g.pose().translate(-cx,-y-h/2f);
        if(!low) RadialPaint.glow(g,cx,y+h,w*.65f,22*unit,Palette.argb(p.accent,Math.round(alpha*.18f)));
        Canvas.rounded(g,x-1,y-1,w+2,h+2,h/2+1,Palette.argb(p.accent,Math.round(alpha*.22f)));
        Canvas.rounded(g,x,y,w,h,h/2,Palette.argb(p.surface0,alpha));
        int fill=Math.round(w*Math.max(0,Math.min(1,motion.progress.value())));
        if(fill>0) {
            Canvas.rounded(g,x,y,fill,h,h/2,Palette.argb(p.accent,alpha));
            if(fill>6) g.fillGradient(x+3,y+1,x+fill-3,y+h/2,(Math.round(alpha*.28f)<<24)|0xffffff,0x00ffffff);
            int tip=x+fill;
            if(animate && !low) RadialPaint.glow(g,tip-3,y+h/2f,10,12,Palette.argb(p.accent,Math.round(alpha*.3f)));
            if(fill>=h) Canvas.rounded(g,tip-5,y+2,3,h-4,2,(Math.round(alpha*.8f)<<24)|0xffffff);
            if(animate && MotionPrefs.particles()) {
                int count=energy>1.2f ? 12:energy<.7f ? 4:8;
                for(int i=0;i<count;i++) if(motion.life[i]>0) {
                    int a=Math.round(alpha*.75f*motion.life[i]/.6f),sx=Math.round(tip+motion.px[i]*energy),sy=Math.round(y+motion.py[i]*energy);
                    g.fill(sx,sy,sx+2,sy+2,Palette.argb(p.accent,a));
                }
            }
        }
        g.pose().popMatrix();
        BrandAtlas.line(g,2,x+Math.round(30*unit),y+Math.round(26*unit),Math.round(60*unit),Palette.argb(p.muted,Math.round(alpha*.8f)));
        BrandAtlas.percent(g,Math.round(raw*100),x+w-17,y+Math.round(24*unit),Palette.argb(p.text,alpha));
    }
}

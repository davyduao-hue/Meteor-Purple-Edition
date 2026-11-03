package dev.meteorpurple.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Presentation only: the real loader owns progress and completion. No artificial delays. */
public final class LoadingBrand {
    private LoadingBrand() {}
    public static void draw(GuiGraphicsExtractor g,float raw,int alpha,LoadingMotion motion) {
        if(alpha<=0) return;
        raw=Math.max(0,Math.min(1,raw)); boolean animate=MotionPrefs.ui();
        motion.update(raw,animate,System.nanoTime());
        float unit=Math.min(1,Math.min(g.guiWidth()/560f,g.guiHeight()/340f));
        int cx=g.guiWidth()/2,cy=Math.round(g.guiHeight()*.45f);
        float enter=motion.enter.value(),lift=(1-enter)*15*unit;
        float bob=animate ? (float)Math.sin(motion.time*2.3)*2:0;
        Palette p=Canvas.palette();
        // Bounded layered washes give depth without any off-screen blur pass.
        for(int i=3;i>0;i--) Canvas.rounded(g,cx-42-i*15,cy-92-i*8,84+i*30,60+i*16,35,Palette.argb(p.accent,Math.round(alpha*.014f)));
        MeteorMark.drawAt(g,cx,cy-72*unit+lift+bob,88*unit*(.85f+.15f*enter),alpha,0, true);
        BrandAtlas.line(g,0,cx,cy+Math.round(5*unit+lift),Math.round(300*unit),(alpha<<24)|0xf4efff);
        BrandAtlas.line(g,1,cx,cy+Math.round(60*unit),Math.round(275*unit),(Math.round(alpha*.75f)<<24)|0xd2c5ec);
        int w=Math.round(Math.min(290*unit,g.guiWidth()-40)),h=Math.max(7,Math.round(10*unit)),x=cx-w/2,y=cy+Math.round(99*unit);
        Canvas.rounded(g,x-2,y+3,w+4,h+2,h/2,Math.round(alpha*.15f)<<24);
        Canvas.rounded(g,x,y,w,h,h/2,(alpha<<24)|0x262539);
        int fill=Math.round(w*Math.max(0,Math.min(1,motion.progress.value())));
        if(fill>0) {
            Canvas.rounded(g,x,y,fill,h,h/2,Palette.argb(p.accent,alpha));
            if(fill>6) g.fillGradient(x+3,y+1,x+fill-3,y+h/2,(Math.round(alpha*.22f)<<24)|0xffffff,0x00ffffff);
            int tip=x+fill;
            Canvas.rounded(g,tip-3,y-1,6,h+2,3,(Math.round(alpha*.6f)<<24)|0xf4eaff);
            if(animate) for(int i=0;i<6;i++) if(motion.life[i]>0) {
                int a=Math.round(alpha*.65f*motion.life[i]/.55f),sx=Math.round(tip+motion.px[i]),sy=Math.round(y+motion.py[i]);
                g.fill(sx,sy,sx+2,sy+2,Palette.argb(p.accent,a));
            }
        }
        BrandAtlas.line(g,2,cx,y+Math.round(25*unit),Math.round(88*unit),(Math.round(alpha*.6f)<<24)|0xd2c5ec);
        BrandAtlas.percent(g,Math.round(raw*100),cx,y+Math.round(47*unit),(Math.round(alpha*.8f)<<24)|0xf4efff);
    }
}

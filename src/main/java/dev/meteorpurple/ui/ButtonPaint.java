package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;

/** Paint only; native controls own focus, narration, click timing and actual hitboxes. */
public final class ButtonPaint {
    private ButtonPaint() {}
    public static void draw(GuiGraphicsExtractor g,AbstractButton b,Palette p,ButtonMotion motion) {
        var mc=Minecraft.getInstance(); Appearance a=Appearance.get();
        var menu=mc.gui.screen() instanceof PurpleTitleScreen m ? m:null;
        boolean inGame=menu==null && mc.level!=null;
        boolean animate=a!=null && (inGame ? a.animateGame():a.animate()),low=MotionPrefs.lowCost();
        float energy=animate ? MotionPrefs.energy():0;
        motion.update(b.isHoveredOrFocused() && b.active,animate,a==null ? 170:a.animationMs(),System.nanoTime());
        float t=motion.amount(),pop=animate ? motion.pop():0;
        int x=b.getX(),y=b.getY(),w=b.getWidth(),h=b.getHeight();
        boolean primary=menu!=null && menu.primary(y) && w>=180;
        float enter=menu!=null ? menu.entrance(y):1;
        float q=enter-1,ease=1+(2+energy*.7f)*q*q*q+(1+energy*.7f)*q*q;
        float scale=1+pop*.024f*energy-motion.pressAmount()*.06f*energy;
        int alpha=Math.round(Math.min(1,enter*3)*255);
        g.pose().pushMatrix(); g.pose().translate(x+w/2f,y+h/2f+(1-ease)*18-pop*2.2f*energy);
        g.pose().scale(scale); g.pose().translate(-x-w/2f,-y-h/2f);
        if(!low) {
            Canvas.rounded(g,x-2,y+4,w+4,h+2,9,Math.round(alpha*.15f)<<24);
            if(t>.01f) RadialPaint.glow(g,x+w/2f,y+h+1,w*.55f,12,Palette.argb(p.accent,Math.round(alpha*t*.11f)));
        }
        Canvas.rounded(g,x,y,w,h,7,Palette.argb(p.accent,Math.round(alpha*(primary ? .62f+t*.32f:.22f+t*.50f))));
        Canvas.rounded(g,x+1,y+1,w-2,h-2,6,Palette.argb(primary ? p.surface2:p.surface0,alpha));
        if(!low) g.fillGradient(x+8,y+1,x+w-8,y+Math.max(3,h/2),Palette.argb(p.text,Math.round(alpha*(primary ? .07f:.025f)+alpha*t*.025f)),0x00ffffff);
        if(t>.005f) {
            int inset=Math.round(9+(w/2f-9)*(1-t));
            Canvas.rounded(g,x+inset,y+h-2,w-inset*2,2,1,Palette.argb(p.accent,Math.round(alpha*t)));
            float sweep=motion.sweep();
            if(sweep>=0 && animate) {
                int sx=x+8+Math.round((w-34)*sweep);
                g.fillGradient(sx,y+3,sx+18,y+h-3,Palette.argb(p.accent,Math.round(alpha*.12f*(1-sweep))),0x00ffffff);
            }
        }
        if(b.isFocused()) {
            Canvas.rounded(g,x-2,y-2,w+4,1,0,Palette.argb(p.accent,alpha));
            Canvas.rounded(g,x-2,y+h+1,w+4,1,0,Palette.argb(p.accent,alpha));
        }
        if(animate && MotionPrefs.particles()) {
            float burst=motion.burst();
            if(burst>=0) for(int i=0;i<6;i++) {
                float distance=(6+burst*18)*energy,sign=i%2==0 ? -1:1;
                int sx=Math.round(x+w/2f+sign*(w*.4f+distance)),sy=Math.round(y+h/2f+(i/2-1)*distance-burst*4);
                g.fill(sx,sy,sx+2,sy+1,Palette.argb(p.accent,Math.round(alpha*(1-burst)*.7f)));
            }
            float ripple=motion.ripple();
            if(ripple>=0) RadialPaint.ring(g,x+w/2f,y+h/2f,w*(.15f+ripple*.4f),h*(.5f+ripple),Palette.argb(p.accent,Math.round(alpha*(1-ripple)*.45f)));
        }
        var text=motion.label(b.getMessage()); int textWidth=motion.labelWidth(mc.font);
        if(primary && w>textWidth+48) {
            int ix=x+14,iy=y+h/2;
            for(int row=-3;row<=3;row++) g.fill(ix,iy+row,ix+4-Math.abs(row),iy+row+1,Palette.argb(p.accent,alpha));
            int arrowX=x+w-20+Math.round(pop*3*energy);
            g.fill(arrowX-3,iy,arrowX+4,iy+1,Palette.argb(p.accent,alpha));
            g.fill(arrowX+2,iy-2,arrowX+3,iy+3,Palette.argb(p.accent,alpha));
        }
        Canvas.fitted(g,text,textWidth,x+w/2,y+(h-9)/2,w-16,Palette.argb(p.text,b.active ? alpha:Math.round(alpha*.4f)));
        g.pose().popMatrix();
    }
}

package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;

public final class ButtonPaint {
    private ButtonPaint() {}
    public static void draw(GuiGraphicsExtractor g,AbstractButton b,Palette p,ButtonMotion motion) {
        var mc=Minecraft.getInstance(); Appearance a=Appearance.get();
        boolean inGame=!(mc.gui.screen() instanceof PurpleTitleScreen) && mc.level!=null;
        boolean animate=a!=null && (inGame ? a.animateGame():a.animate());
        motion.update(b.isHoveredOrFocused() && b.active,animate,a==null ? 170:a.animationMs(),System.nanoTime());
        float t=motion.amount(),pop=animate ? motion.pop():0;
        int x=b.getX(),y=b.getY(),w=b.getWidth(),h=b.getHeight();
        float enter=mc.gui.screen() instanceof PurpleTitleScreen menu ? menu.entrance(y):1;
        float q=enter-1,ease=1+2.7f*q*q*q+1.7f*q*q,scale=1+pop*.018f-motion.pressAmount()*.035f;
        int alpha=Math.round(Math.min(1,enter*3)*255);
        g.pose().pushMatrix(); g.pose().translate(x+w/2f,y+h/2f+(1-ease)*12-pop*1.6f); g.pose().scale(scale); g.pose().translate(-x-w/2f,-y-h/2f);
        Canvas.rounded(g,x-1,y+3,w+2,h+1,7,Math.round(alpha*.20f)<<24);
        Canvas.rounded(g,x,y,w,h,6,Palette.argb(p.accent,Math.round(alpha*(.13f+t*.30f))));
        Canvas.rounded(g,x+1,y+1,w-2,h-2,5,Palette.argb(p.light ? p.surface0:p.base,Math.round(alpha*.96f)));
        if(t>.01f) {
            int inset=Math.round(8+(w/2f-8)*(1-t));
            Canvas.rounded(g,x+inset,y+h-2,w-inset*2,2,1,Palette.argb(p.accent,Math.round(alpha*t)));
            float sweep=motion.sweep();
            if(sweep>=0 && animate) {
                int sx=x+8+Math.round((w-32)*sweep);
                g.fillGradient(sx,y+3,sx+15,y+h-3,Palette.argb(p.accent,Math.round(alpha*.07f)),0x00ffffff);
            }
        }
        var text=motion.label(b.getMessage());
        g.text(mc.font,text,x+(w-mc.font.width(text))/2,y+(h-9)/2,Palette.argb(p.text,b.active ? alpha:Math.round(alpha*.4f)),false);
        g.pose().popMatrix();
    }
}

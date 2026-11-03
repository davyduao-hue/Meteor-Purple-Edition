package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;

public final class ButtonPaint {
    private ButtonPaint() {}
    public static void draw(GuiGraphicsExtractor g,AbstractButton b,Palette p,ButtonMotion motion) {
        Appearance a=Appearance.get();
        motion.update(b.isHoveredOrFocused() && b.active,a!=null && a.animate(),a==null ? 170:a.animationMs(),System.nanoTime());
        float t=motion.amount(); int x=b.getX(),y=b.getY(),w=b.getWidth(),h=b.getHeight();
        // Rectangular vanilla proportions, with only four cheap panel/edge fills.
        g.fill(x,y,x+w,y+h,Palette.argb(p.light ? p.text:p.accent,(int)(38+t*75)));
        g.fill(x+1,y+1,x+w-1,y+h-1,Palette.argb(p.base,247));
        g.fill(x+1,y+1,x+w-1,y+h-1,Palette.argb(p.accent,(int)(t*20)));
        if(t>0) {
            int inset=Math.round((1-t)*(w/2f-8));
            g.fill(x+inset,y+h-2,x+w-inset,y+h,Palette.argb(p.accent,(int)(t*255)));
            float sweep=motion.sweep();
            if(sweep>=0 && a!=null && a.animate()) {
                int left=x+inset,right=x+w-inset,head=left+Math.round((right-left+24)*sweep)-24;
                g.fill(Math.max(left,head),y+h-2,Math.max(left,Math.min(right,head+20)),y+h,Palette.argb(p.text,(int)(t*180)));
            }
        }
        var font=Minecraft.getInstance().font;
        g.text(font,b.getMessage(),x+(w-font.width(b.getMessage()))/2,y+(h-9)/2,Palette.argb(p.text,b.active ? 255:105),false);
    }
}

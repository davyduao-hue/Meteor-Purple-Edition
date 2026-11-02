package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class PurpleButton extends Button {
    private final Palette preview;
    private float hover;
    private long previousNanos;
    public PurpleButton(int x,int y,int w,int h,String title,Runnable action) { this(x,y,w,h,title,action,null); }
    public PurpleButton(int x,int y,int w,int h,String title,Runnable action,Palette preview) {
        super(x,y,w,h,Canvas.label(title),ignored -> action.run(),DEFAULT_NARRATION);
        this.preview=preview;
    }
    @Override protected void extractContents(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
        Palette p=preview==null ? Canvas.palette():preview;
        long now=System.nanoTime();
        float target=isHoveredOrFocused() && active ? 1:0;
        Appearance a=Appearance.get();
        if (a==null || !a.animate()) hover=target;
        else hover+=(target-hover)*Math.min(1,Math.max(0,(now-previousNanos)/1_000_000_000f)*12);
        previousNanos=now;
        int x=getX(),y=getY(),w=getWidth(),h=getHeight();
        Canvas.rounded(g,x,y,w,h,Math.min(12,h/2),Palette.argb(p.accent,(int)(55+hover*95)));
        Canvas.rounded(g,x+1,y+1,w-2,h-2,Math.min(11,h/2),Palette.argb(hover>.5 ? p.surface1:p.surface0,245));
        if (hover>.05) g.fill(x+12,y+1,x+w-12,y+2,Palette.argb(p.accent,(int)(hover*180)));
        Canvas.centered(g,getMessage(),x+w/2,y+(h-9)/2,Palette.argb(p.text,active ? 255:110));
    }
}

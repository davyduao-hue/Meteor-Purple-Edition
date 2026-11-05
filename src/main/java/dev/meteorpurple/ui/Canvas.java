package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

/** Native batched fills; rounded-corner geometry is cached, with no textures to allocate. */
public final class Canvas {
    public static final FontDescription FONT=new FontDescription.Resource(Identifier.fromNamespaceAndPath("meteor-purple-edition","ui"));
    public static Component label(String text) { return Component.literal(text).withStyle(style -> style.withFont(FONT)); }
    public static void centered(GuiGraphicsExtractor g,Component text,int x,int y,int color) {
        var font=Minecraft.getInstance().font;
        g.text(font,text,x-font.width(text)/2,y,color,false);
    }
    public static void fitted(GuiGraphicsExtractor g,Component text,int measuredWidth,int cx,int y,int available,int color) {
        float scale=Math.min(1,available/(float)Math.max(1,measuredWidth));
        g.pose().pushMatrix(); g.pose().translate(cx,y+(1-scale)*4); g.pose().scale(scale);
        g.text(Minecraft.getInstance().font,text,-measuredWidth/2,0,color,false); g.pose().popMatrix();
    }
    private Canvas() {}
    public static Palette palette() { return Appearance.get()==null ? Appearance.Style.Mocha.palette:Appearance.get().palette(); }
    public static void rounded(GuiGraphicsExtractor g,int x,int y,int w,int h,int radius,int color) {
        if(w<=0 || h<=0 || (color>>>24)==0) return;
        int scale=Appearance.get()!=null && Appearance.get().performance.get() ? 1:Minecraft.getInstance().getWindow().getGuiScale();
        var pose=new org.joml.Matrix3x2f(g.pose()).scale(1f/scale);
        x*=scale; y*=scale; w*=scale; h*=scale; radius*=scale;
        int r=Math.max(0,Math.min(96,Math.min(radius,Math.min(w,h)/2)));
        var clip=g.scissorStack.peek();
        var bounds=new net.minecraft.client.gui.navigation.ScreenRectangle(x,y,w,h).transformMaxBounds(pose);
        if(clip!=null) bounds=bounds.intersection(clip);
        if(bounds!=null && bounds.width()>0 && bounds.height()>0)
            g.guiRenderState.addGuiElement(new RoundedPanel(pose,x,y,w,h,r,color,clip,bounds));
    }
    public static void background(GuiGraphicsExtractor g,int w,int h) {
        Palette p=palette();
        g.fillGradient(0,0,w,h,Palette.argb(p.mantle,255),Palette.argb(p.surface1,255));
        // Static washes keep the home screen quiet and cheap even at uncapped FPS.
        g.fillGradient(0,0,w,h,Palette.argb(p.accent,p.light ? 35:20),0x00000000);
        g.fillGradient(0,h/2,w,h,0x00000000,Palette.argb(p.accent,p.light ? 18:8));
    }
    public static void card(GuiGraphicsExtractor g,int x,int y,int w,int h,Palette p) {
        Appearance a=Appearance.get();
        if (a==null || !a.performance.get()) rounded(g,x-3,y+4,w+6,h+4,24,p.light ? 0x12000000:0x30000000);
        rounded(g,x,y,w,h,22,Palette.argb(p.light ? p.text:p.accent,p.light ? 30:65));
        rounded(g,x+1,y+1,w-2,h-2,21,Palette.argb(p.base,238));
        g.fillGradient(x+22,y+1,x+w-22,y+2,p.light ? 0xbfffffff:0x65ffffff,0x00ffffff);
    }
}

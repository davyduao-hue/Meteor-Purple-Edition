package dev.meteorpurple.ui;
import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** A live miniature of the theme's actual window, icon and control language. */
public final class ThemeCard extends PurpleButton {
    private final Appearance.Style style;
    private final Component title,caption;
    private final ButtonMotion motion=new ButtonMotion();
    public ThemeCard(int x,int y,int w,int h,Appearance.Style style,Runnable action) {
        super(x,y,w,h,style.title,action); this.style=style; title=Canvas.label(style.title); caption=Canvas.label(style.subtitle);
        setTooltip(net.minecraft.client.gui.components.Tooltip.create(Component.literal(style.subtitle)));
    }
    @Override protected void extractContents(GuiGraphicsExtractor g,int mx,int my,float delta) {
        var mc=Minecraft.getInstance(); var p=style.palette; var design=ThemeDesign.of(style);
        boolean selected=Appearance.get().style.get()==style;
        motion.update(isHoveredOrFocused(),MotionPrefs.ui(),170,System.nanoTime());
        int x=getX(),y=getY(),w=getWidth(),h=getHeight(),r=Math.min(12,design.controlRadius());
        Canvas.rounded(g,x,y,w,h,r,Palette.argb(p.accent,selected ? 240:(int)(60+motion.amount()*110)));
        Canvas.rounded(g,x+1,y+1,w-2,h-2,Math.max(1,r-1),Palette.argb(p.base,255));
        int iconSize=h>=34 ? 30:20,iy=y+(h-iconSize)/2;
        ThemeIcons.draw(g,style,ThemeIcons.Icon.LOGO,x+5,iy,iconSize,255);
        int tx=x+iconSize+10,preview=h>=38 && w>=240 ? 60:0,available=w-(tx-x)-preview-10;
        Canvas.fitted(g,title,mc.font.width(title),tx+available/2,y+(h>=38 ? 9:(h-9)/2),available,Palette.argb(p.text,255));
        if(h>=38) {
            g.pose().pushMatrix(); g.pose().translate(tx,y+24); g.pose().scale(.72f);
            Canvas.fitted(g,caption,mc.font.width(caption),(int)(available/.72f/2),0,(int)(available/.72f),Palette.argb(p.muted,230));
            g.pose().popMatrix();
        }
        if(preview>0) {
            int px=x+w-57,py=y+7;
            Canvas.rounded(g,px,py,49,h-14,Math.min(7,design.windowRadius()),Palette.argb(p.mantle,255));
            g.fill(px+5,py+5,px+43,py+6,Palette.argb(p.accent,90));
            ThemeIcons.draw(g,style,ThemeIcons.Icon.SETTING,px+4,py+9,17,255);
            Canvas.rounded(g,px+26,py+15,15,7,4,Palette.argb(p.accent,255));
            Canvas.rounded(g,px+35,py+17,3,3,2,Palette.argb(p.base,255));
        }
        if(selected) Canvas.rounded(g,x+6,y+5,2,h-10,1,Palette.argb(p.accent,255));
    }
}

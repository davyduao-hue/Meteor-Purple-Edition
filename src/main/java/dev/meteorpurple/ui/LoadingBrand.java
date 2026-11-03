package dev.meteorpurple.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Vanilla font works during the first resource reload, before custom fonts are ready. */
public final class LoadingBrand {
    private static final Component TITLE=Component.literal("Zenith Client").withStyle(s -> s.withBold(true));
    private static final Component TAGLINE=Component.literal("the client for the elite");
    private LoadingBrand() {}
    public static void draw(GuiGraphicsExtractor g,int alpha) {
        if(alpha<=0) return;
        var font=Minecraft.getInstance().font;
        int cx=g.guiWidth()/2,cy=g.guiHeight()/2;
        float scale=Math.min(2.5f,Math.max(1.5f,g.guiWidth()/210f));
        g.pose().pushMatrix(); g.pose().translate(cx,cy-8); g.pose().scale(scale);
        g.text(font,TITLE,-font.width(TITLE)/2,-7,(alpha<<24)|0xf1ecff,false);
        g.pose().popMatrix();
        g.text(font,TAGLINE,cx-font.width(TAGLINE)/2,cy+25,(alpha<<24)|0xb9acd9,false);
        g.fill(cx-24,cy+44,cx+24,cy+46,(alpha<<24)|0xb49af0);
    }
    public static void worldCaption(GuiGraphicsExtractor g) {
        var font=Minecraft.getInstance().font;
        g.text(font,TITLE,(g.guiWidth()-font.width(TITLE))/2,20,0xffeee7ff,false);
        g.text(font,TAGLINE,(g.guiWidth()-font.width(TAGLINE))/2,35,0xffc6b8e8,false);
    }
}

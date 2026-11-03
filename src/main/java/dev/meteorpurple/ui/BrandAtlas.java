package dev.meteorpurple.ui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import java.io.IOException;

/** Pre-rasterized Comfortaa typography is available even during the first font reload. */
public final class BrandAtlas {
    public static final Identifier ID=Identifier.fromNamespaceAndPath("meteor-purple-edition","zenith-bootstrap-atlas");
    private static TextureManager registered;
    private BrandAtlas() {}
    public static void register(TextureManager manager) {
        if(registered==manager) return;
        try(var in=BrandAtlas.class.getResourceAsStream("/assets/meteor-purple-edition/textures/ui/brand-atlas.png")) {
            if(in==null) throw new IOException("Missing Zenith type atlas");
            manager.register(ID,new DynamicTexture(() -> "Zenith Comfortaa type",NativeImage.read(in)));
            registered=manager;
        } catch(IOException e) { throw new IllegalStateException("Could not load Zenith typography",e); }
    }
    public static void line(GuiGraphicsExtractor g,int row,int cx,int y,int width,int color) {
        int sy=row==0 ? 0:row==1 ? 192:272,sh=row==0 ? 192:row==1 ? 80:48;
        int sx=row==2 ? 384:0,sw=row==2 ? 256:1024;
        int h=Math.round(width*sh/(float)sw);
        g.blit(RenderPipelines.GUI_TEXTURED,ID,cx-width/2,y,sx,sy,width,h,sw,sh,1024,512,color);
    }
    public static void percent(GuiGraphicsExtractor g,int value,int cx,int y,int color) {
        value=Math.max(0,Math.min(100,value)); int digits=value>=100 ? 3:value>=10 ? 2:1;
        int left=cx-(digits+1)*9/2;
        for(int i=0;i<digits;i++) {
            int place=digits-i-1,divisor=place==2 ? 100:place==1 ? 10:1;
            glyph(g,(value/divisor)%10,left+i*9,y,color);
        }
        glyph(g,10,left+digits*9,y,color);
    }
    private static void glyph(GuiGraphicsExtractor g,int index,int x,int y,int color) {
        g.blit(RenderPipelines.GUI_TEXTURED,ID,x,y,index*64,320,10,13,64,80,1024,512,color);
    }
}

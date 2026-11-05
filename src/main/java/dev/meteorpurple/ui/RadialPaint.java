package dev.meteorpurple.ui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

/** Feathered light and thin elliptical rings, one state each; no blur/framebuffer pass. */
public final class RadialPaint {
    private static final int SIDES=48;
    private static final float[] X=new float[SIDES+1],Y=new float[SIDES+1];
    static { for(int i=0;i<=SIDES;i++) { X[i]=(float)Math.cos(i*Math.PI*2/SIDES); Y[i]=(float)Math.sin(i*Math.PI*2/SIDES); } }
    private RadialPaint() {}
    public static void glow(GuiGraphicsExtractor g,float x,float y,float rx,float ry,int color) { draw(g,x,y,rx,ry,0,color,color&0xffffff); }
    public static void ring(GuiGraphicsExtractor g,float x,float y,float rx,float ry,int color) { draw(g,x,y,rx,ry,.985f,color,color); }
    private static void draw(GuiGraphicsExtractor g,float x,float y,float rx,float ry,float inner,int color,int edge) {
        if(rx<=0 || ry<=0 || color>>>24==0) return;
        Matrix3x2f pose=new Matrix3x2f(g.pose()); var clip=g.scissorStack.peek();
        var bounds=new ScreenRectangle((int)Math.floor(x-rx),(int)Math.floor(y-ry),(int)Math.ceil(rx*2+2),(int)Math.ceil(ry*2+2)).transformMaxBounds(pose);
        if(clip!=null) bounds=bounds.intersection(clip);
        if(bounds!=null) g.guiRenderState.addGuiElement(new State(pose,x,y,rx,ry,inner,color,edge,clip,bounds));
    }
    private record State(Matrix3x2fc pose,float x,float y,float rx,float ry,float inner,int color,int edge,
                         ScreenRectangle scissorArea,ScreenRectangle bounds) implements GuiElementRenderState {
        public RenderPipeline pipeline() { return RenderPipelines.GUI; }
        public TextureSetup textureSetup() { return TextureSetup.noTexture(); }
        public void buildVertices(VertexConsumer v) {
            for(int i=0;i<SIDES;i++) {
                v.addVertexWith2DPose(pose,x+X[i]*rx*inner,y+Y[i]*ry*inner).setColor(color);
                v.addVertexWith2DPose(pose,x+X[i+1]*rx*inner,y+Y[i+1]*ry*inner).setColor(color);
                v.addVertexWith2DPose(pose,x+X[i+1]*rx,y+Y[i+1]*ry).setColor(edge);
                v.addVertexWith2DPose(pose,x+X[i]*rx,y+Y[i]*ry).setColor(edge);
            }
        }
    }
}

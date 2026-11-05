package dev.meteorpurple.ui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2fc;

/** A whole rounded surface is ONE native GUI state, with ordinary batched GUI vertices.
 * Snapshot the pose at extraction: the render thread consumes this after the UI returns. */
public record RoundedPanel(Matrix3x2fc pose,int x,int y,int width,int height,int radius,int color,
                           ScreenRectangle scissorArea,ScreenRectangle bounds) implements GuiElementRenderState {
    @Override public RenderPipeline pipeline() { return RenderPipelines.GUI; }
    @Override public TextureSetup textureSetup() { return TextureSetup.noTexture(); }
    @Override public void buildVertices(VertexConsumer v) {
        if(height>radius*2) quad(v,x,y+radius,x+width,y+height-radius);
        int[] spans=RoundedGeometry.spans(radius);
        for(int i=0;i<spans.length;i+=3) {
            int start=spans[i],end=spans[i+1],inset=spans[i+2];
            if(width<=inset*2) continue;
            quad(v,x+inset,y+start,x+width-inset,y+end);
            quad(v,x+inset,y+height-end,x+width-inset,y+height-start);
        }
    }
    private void quad(VertexConsumer v,int x0,int y0,int x1,int y1) {
        v.addVertexWith2DPose(pose,x0,y0).setColor(color);
        v.addVertexWith2DPose(pose,x0,y1).setColor(color);
        v.addVertexWith2DPose(pose,x1,y1).setColor(color);
        v.addVertexWith2DPose(pose,x1,y0).setColor(color);
    }
}

package dev.meteorpurple.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2f;

/** Meteor's seven logo faces, drawn as cached native affine quads, sharp at every scale.
 * Geometry follows the logo in the bundled GPL Meteor source; no off-screen texture or shader. */
public final class MeteorMark {
    private static final Matrix3x2f[] FACES={
        face(0,30,50,0,337,223), face(0,30,337,223,0,87),
        face(200,30,250,0,437,166), face(100,202,337,339,100,260),
        face(400,260,600,145,600,376), face(400,260,600,376,400,491),
        face(600,376,800,260,600,606)
    };
    private static final int[] COLORS={0xba43e2,0x6c29be,0xba43e2,0x6c29be,0xba43e2,0x6c29be,0x9139dc};
    private MeteorMark() {}
    private static Matrix3x2f face(float x0,float y0,float x1,float y1,float x3,float y3) {
        return new Matrix3x2f(x1-x0,y1-y0,x3-x0,y3-y0,x0,y0);
    }
    public static void draw(GuiGraphicsExtractor g,int width,int height) {
        float w=Math.min(width-32,height*1.12f),scale=w/800f;
        g.pose().pushMatrix();
        g.pose().translate((width-w)/2,(height-606*scale)/2);
        g.pose().scale(scale);
        for(int i=0;i<FACES.length;i++) {
            g.pose().pushMatrix(); g.pose().mul(FACES[i]);
            g.fill(0,0,1,1,0x78000000|COLORS[i]);
            g.pose().popMatrix();
        }
        g.pose().popMatrix();
    }
}

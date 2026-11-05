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
    private MeteorMark() {}
    private static Matrix3x2f face(float x0,float y0,float x1,float y1,float x3,float y3) {
        return new Matrix3x2f(x1-x0,y1-y0,x3-x0,y3-y0,x0,y0);
    }
    public static void drawAt(GuiGraphicsExtractor g,float cx,float cy,float width,int alpha,float tilt,boolean shadow) {
        draw(g,cx,cy,width,alpha,tilt,shadow,0,-1);
    }
    public static void drawAssembled(GuiGraphicsExtractor g,float cx,float cy,float width,int alpha,float tilt,float assembly) {
        draw(g,cx,cy,width,alpha,tilt,!MotionPrefs.lowCost(),assembly,-1);
    }
    public static void drawExpressive(GuiGraphicsExtractor g,float cx,float cy,float width,int alpha,float tilt,float assembly,float shine) {
        draw(g,cx,cy,width,alpha,tilt,!MotionPrefs.lowCost(),assembly,shine);
    }
    private static void draw(GuiGraphicsExtractor g,float cx,float cy,float width,int alpha,float tilt,boolean shadow,float assembly,float shine) {
        if(alpha<=0) return;
        alpha=Math.max(0,Math.min(255,alpha));
        // Recolor the actual Meteor geometry, so every selected palette/custom accent is coherent.
        var accent=Canvas.palette().accent;
        if(shadow) {
            g.pose().pushMatrix(); g.pose().translate(cx,cy+width*.36f); g.pose().scale(width/800f,width/4800f);
            g.pose().translate(-400,-303);
            for(var face:FACES) { g.pose().pushMatrix(); g.pose().mul(face); g.fill(0,0,1,1,Math.round(alpha*.20f)<<24); g.pose().popMatrix(); }
            g.pose().popMatrix();
        }
        g.pose().pushMatrix(); g.pose().translate(cx,cy); g.pose().rotate(tilt); g.pose().scale(width/800f); g.pose().translate(-400,-303);
        for(int i=0;i<FACES.length;i++) {
            float split=assembly*assembly*(1+i*.08f),shade=i==1 || i==3 || i==5 ? .60f:i==6 ? .80f:1;
            int rgb=Math.round(accent.r*shade)<<16 | Math.round(accent.g*shade)<<8 | Math.round(accent.b*shade);
            g.pose().pushMatrix();
            g.pose().translate((i<4 ? -180:100)*split,(i<4 ? -80:120)*split);
            g.pose().mul(FACES[i]); g.fill(0,0,1,1,(Math.round(alpha*(1-assembly*.7f))<<24)|rgb);
            if(shine>=0 && shine<=1) {
                int sx=(int)(shine*1700)-i*80-100,left=Math.max(0,sx),right=Math.min(1000,sx+85);
                if(right>left) { g.pose().scale(.001f); g.fill(left,0,right,1000,(Math.round(alpha*.20f)<<24)|0xffffff); }
            }
            g.pose().popMatrix();
        }
        g.pose().popMatrix();
    }
}

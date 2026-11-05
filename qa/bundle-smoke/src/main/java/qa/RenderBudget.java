package qa;

import dev.meteorpurple.ui.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import java.nio.file.*;
import java.util.Arrays;

/** In-process extraction microbenchmark, NOT a gameplay FPS benchmark. Both paths draw identical panels. */
final class RenderBudget {
    static void run(Minecraft mc,Path out) {
        int[] counts=new int[2]; double[][] times=new double[2][9];
        for(int sample=-3;sample<9;sample++) for(int path=0;path<2;path++) {
            var state=new GuiRenderState(); var g=new GuiGraphicsExtractor(mc,state,0,0);
            long start=System.nanoTime();
            for(int i=0;i<200;i++) {
                int x=i%20*30,y=i/20*30;
                if(path==1) Canvas.rounded(g,x,y,28,24,7,0xffa090cf);
                else legacy(g,mc,x,y,28,24,7,0xffa090cf);
            }
            double ms=(System.nanoTime()-start)/1_000_000.;
            int[] counter={0}; state.forEachElement(e -> counter[0]++,GuiRenderState.TraverseRange.ALL);
            counts[path]=counter[0]; if(sample>=0) times[path][sample]=ms;
        }
        for(double[] path:times) Arrays.sort(path);
        if(counts[1]!=200 || counts[0]<=counts[1]) throw new AssertionError("one-state rounded panel budget");
        String report="200 identical 28x24 radius-7 panels, GUI scale "+mc.getWindow().getGuiScale()+"\n"
            +"Legacy native GUI states: "+counts[0]+"\nNew native GUI states: "+counts[1]+"\n"
            +"Legacy extraction median ms (9 samples after 3 warmups): "+times[0][4]+"\n"
            +"New extraction median ms: "+times[1][4]+"\n"
            +"This measures panel extraction only, not GPU frame rate, whole menu performance or gameplay FPS.\n";
        try { Files.writeString(out.resolve("render-budget.txt"),report); } catch(Exception e) { throw new RuntimeException(e); }
        System.out.println("PURPLE_SMOKE "+report.replace('\n',' '));
    }
    private static void legacy(GuiGraphicsExtractor g,Minecraft mc,int x,int y,int w,int h,int r,int color) {
        int scale=mc.getWindow().getGuiScale();
        g.pose().pushMatrix(); g.pose().scale(1f/scale); x*=scale; y*=scale; w*=scale; h*=scale; r*=scale;
        g.fill(x,y+r,x+w,y+h-r,color);
        for(int row=0;row<r;row++) {
            int inset=(int)Math.ceil(r-Math.sqrt(r*r-Math.pow(r-row-.5,2)));
            g.fill(x+inset,y+row,x+w-inset,y+row+1,color);
            g.fill(x+inset,y+h-row-1,x+w-inset,y+h-row,color);
        }
        g.pose().popMatrix();
    }
}

package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.TitleScreen;

/** Native Minecraft actions and layout; Meteor is the only large header mark. */
public final class PurpleTitleScreen extends TitleScreen {
    private long opened=System.nanoTime(),previous;
    private float elapsed,mouseX,mouseY;
    private final Spring logoEnter=new Spring(0),followX=new Spring(0),followY=new Spring(0);
    public PurpleTitleScreen() {
        super(false,new LogoRenderer(true) {
            @Override public void extractRenderState(GuiGraphicsExtractor g,int width,float alpha) {
                if(Minecraft.getInstance().gui.screen() instanceof PurpleTitleScreen menu) menu.header(g,alpha);
            }
        });
    }
    @Override public void added() { super.added(); opened=System.nanoTime(); logoEnter.reset(0); previous=0; }
    @Override protected void init() {
        super.init();
        addRenderableWidget(new PurpleButton(8,8,70,20,"Themes",() -> minecraft.gui.setScreen(new ThemeGalleryScreen(this))));
    }
    public boolean colosseum() { return Appearance.get()!=null && Appearance.get().menuBackground.get()==Appearance.Backdrop.Colosseum; }
    public float entrance(int y) {
        if(!MotionPrefs.ui()) return 1;
        float delay=Math.max(0,y-(height/4+48))*.0012f;
        return Math.max(0,Math.min(1,(elapsed-.05f-delay)/.38f));
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int x,int y,float delta) {
        long now=System.nanoTime(); elapsed=(now-opened)/1_000_000_000f;
        float dt=previous==0 ? 1/60f:Math.max(0,Math.min(.1f,(now-previous)/1_000_000_000f)); previous=now;
        boolean animate=MotionPrefs.ui();
        if(animate) {
            logoEnter.update(1,dt,12); followX.update((x-width/2f)/width*3,dt,10); followY.update((y-70f)/height*2,dt,10);
        } else { logoEnter.reset(1); followX.reset(0); followY.reset(0); }
        super.extractRenderState(g,x,y,delta);
    }
    @Override protected void extractPanorama(GuiGraphicsExtractor g,float delta) {
        if(colosseum()) {
            if(!MotionPrefs.ui()) minecraft.gameRenderer.gameRenderState().optionsRenderState.panoramaSpeed=0;
            minecraft.gameRenderer.panorama().extractRenderState(g,width,height);
            g.fillGradient(0,0,width,height,0x44090b18,0x88090b18);
        } else {
            g.fillGradient(0,0,width,height,0xff11111c,0xff191724);
            var p=Canvas.palette();
            for(int i=3;i>0;i--) Canvas.rounded(g,width/2-60-i*20,20-i*8,120+i*40,100+i*16,45,Palette.argb(p.accent,3));
            for(int i=0;i<5;i++) {
                int x=(int)(width*(.17+i*.165)),y=24+(i*37)%100;
                int a=MotionPrefs.ui() ? (int)(28+16*Math.sin(elapsed*1.6+i*2)):30;
                g.fill(x,y,x+1,y+1,Palette.argb(p.accent,a));
            }
        }
    }
    private void header(GuiGraphicsExtractor g,float alpha) {
        float entry=logoEnter.value(),bob=MotionPrefs.ui() ? (float)Math.sin(elapsed*2)*1.7f:0;
        float tilt=MotionPrefs.ui() ? (float)Math.sin(elapsed*1.3)*.018f:0;
        float w=Math.min(100,Math.max(76,height*.25f));
        MeteorMark.drawAt(g,width/2f+followX.value(),64+followY.value()+bob+(1-entry)*12,w*(.85f+.15f*entry),Math.round(alpha*255),tilt,true);
    }
}

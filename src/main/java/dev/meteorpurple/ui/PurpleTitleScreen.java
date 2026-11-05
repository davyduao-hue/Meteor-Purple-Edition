package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;

/** Native Minecraft actions and layout; Meteor is the only large header mark. */
public final class PurpleTitleScreen extends TitleScreen {
    private long opened=System.nanoTime(),previous;
    private float elapsed,logoClicked=-10;
    private final Spring logoEnter=new Spring(0),logoHover=new Spring(0),followX=new Spring(0),followY=new Spring(0);
    private final Spring logoBounce=new Spring(0);
    private final MenuAtmosphere atmosphere=new MenuAtmosphere();
    private int firstButtonY;
    private float heroY=64;
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
        firstButtonY=height/4+48;
        heroY=height>=300 ? Math.max(56,(height-290)/2f+44):56;
        // Keep the native controls/actions. Reflow their real hitboxes along with their paint.
        if(height>=300 && width>=360) {
            int nativeTop=firstButtonY; firstButtonY=Math.round(heroY+74);
            for(var child:children()) if(child instanceof AbstractWidget b && b.getY()>=nativeTop && b.getY()<=nativeTop+96) {
                int row=Math.round((b.getY()-nativeTop)/24f);
                b.setY(firstButtonY+row*28);
                if(b.getWidth()==200) { b.setX(width/2-110); b.setWidth(220); b.setHeight(24); }
                else if(b.getWidth()==98) { b.setX(b.getX()<width/2 ? width/2-110:width/2+2); b.setWidth(108); b.setHeight(24); }
            }
        }
        addRenderableWidget(new PurpleButton(8,8,70,20,"Themes",() -> minecraft.gui.setScreen(new ThemeGalleryScreen(this))));
        addRenderableWidget(new PurpleButton(width-88,8,80,20,"Motion",() -> minecraft.gui.setScreen(new MotionScreen(this))));
    }
    public boolean colosseum() { return Appearance.get()!=null && Appearance.get().menuBackground.get()==Appearance.Backdrop.Colosseum; }
    public float entrance(int y) {
        if(!MotionPrefs.ui()) return 1;
        float delay=Math.max(0,y-firstButtonY)*.002f;
        return Math.max(0,Math.min(1,(elapsed-.10f-delay)/.52f));
    }
    public boolean primary(int y) { return y==firstButtonY; }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int x,int y,float delta) {
        long now=System.nanoTime();
        // Resource reload can draw the title behind an opaque overlay for several seconds.
        // Start its entrance when it becomes visible, rather than spending it behind loading.
        if(minecraft.gui.overlay()!=null) { opened=now; previous=0; }
        elapsed=(now-opened)/1_000_000_000f;
        float dt=previous==0 ? 1/60f:Math.max(0,Math.min(.1f,(now-previous)/1_000_000_000f)); previous=now;
        boolean animate=MotionPrefs.ui();
        if(animate) {
            float energy=MotionPrefs.energy();
            logoEnter.update(1,dt,11); logoHover.update(Math.abs(x-width/2f)<65 && Math.abs(y-heroY)<48 ? 1:0,dt,14);
            logoBounce.update(0,dt,16);
            followX.update(Math.max(-1,Math.min(1,(x-width/2f)/width))*8*energy,dt,10);
            followY.update(Math.max(-1,Math.min(1,(y-heroY)/height))*4*energy,dt,10);
        } else { logoEnter.reset(1); logoHover.reset(0); logoBounce.reset(0); followX.reset(0); followY.reset(0); }
        super.extractRenderState(g,x,y,delta);
    }
    @Override public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event,boolean twice) {
        if(MotionPrefs.ui() && event.button()==0 && Math.abs(event.x()-width/2f)<60 && Math.abs(event.y()-heroY)<44) {
            logoBounce.kick(4); logoClicked=elapsed; return true;
        }
        return super.mouseClicked(event,twice);
    }
    @Override protected void extractPanorama(GuiGraphicsExtractor g,float delta) {
        if(colosseum()) {
            if(!MotionPrefs.ui()) minecraft.gameRenderer.gameRenderState().optionsRenderState.panoramaSpeed=0;
            minecraft.gameRenderer.panorama().extractRenderState(g,width,height);
            g.fillGradient(0,0,width,height,0x44090b18,0x88090b18);
        } else {
            atmosphere.draw(g,width,height,heroY,elapsed,followX.value(),followY.value());
        }
    }
    private void header(GuiGraphicsExtractor g,float alpha) {
        boolean animate=MotionPrefs.ui(); float energy=MotionPrefs.energy();
        float entry=logoEnter.value(),bob=animate ? (float)Math.sin(elapsed*1.9)*2.4f*energy:0;
        float tilt=animate ? (float)Math.sin(elapsed*1.15)*.024f*energy+followX.value()*.004f:0;
        float w=Math.min(110,Math.max(76,height*.27f));
        float click=elapsed-logoClicked;
        if(animate && click>=0 && click<.65f && MotionPrefs.particles()) RadialPaint.ring(g,width/2f,heroY+44,90+click*80,17+click*15,Palette.argb(Canvas.palette().accent,(int)((1-click/.65f)*80*alpha)));
        float shine=animate ? (elapsed%6.5f-3.5f)/.8f:-1;
        MeteorMark.drawExpressive(g,width/2f+followX.value(),heroY+followY.value()+bob+(1-entry)*18,w*(.82f+.18f*entry+logoHover.value()*.055f+logoBounce.value()*MotionPrefs.energy()),Math.round(alpha*255),tilt,
            animate ? Math.max(0,1-elapsed/.9f):0,shine);
    }
}

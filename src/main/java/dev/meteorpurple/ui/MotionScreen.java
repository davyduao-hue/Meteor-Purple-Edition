package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Controls are reachable before entering a world; no technical settings hunt required. */
public final class MotionScreen extends Screen {
    private static final Component HEADING=Canvas.label("Make it feel like you");
    private static final Component CAPTION=Canvas.label("Try a style. Your choice saves automatically.");
    private final Screen parent;
    public MotionScreen(Screen parent) { super(Component.literal("Zenith motion")); this.parent=parent; }
    @Override protected void init() {
        Appearance a=Appearance.get(); int w=Math.min(260,width-32),x=(width-w)/2,y=Math.max(48,height/2-76);
        for(var style:Appearance.Motion.values()) {
            var selected=style==a.motionStyle.get();
            addRenderableWidget(new PurpleButton(x,y,w,24,(selected ? "\u2022  ":"")+style.name(),() -> {
                a.motionStyle.set(style); a.uiAnimations.set(true); a.reducedMotion.set(false); a.performance.set(false); rebuildWidgets();
            })); y+=29;
        }
        addRenderableWidget(new PurpleButton(x,y+3,w/2-3,22,"Particles: "+(a.particles.get() ? "on":"off"),() -> { a.particles.set(!a.particles.get()); rebuildWidgets(); }));
        addRenderableWidget(new PurpleButton(x+w/2+3,y+3,w/2-3,22,"Game UI: "+(a.gameAnimations.get() ? "on":"off"),() -> { a.gameAnimations.set(!a.gameAnimations.get()); rebuildWidgets(); }));
        addRenderableWidget(new PurpleButton(x,y+30,w/2-3,22,"Reduce motion",() -> { a.reducedMotion.set(true); rebuildWidgets(); }));
        addRenderableWidget(new PurpleButton(x+w/2+3,y+30,w/2-3,22,"Performance",() -> { a.performance.set(true); rebuildWidgets(); }));
        addRenderableWidget(new PurpleButton(x,y+60,w,22,"Done",this::onClose));
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
        Canvas.background(g,width,height); Palette p=Canvas.palette();
        Canvas.centered(g,HEADING,width/2,16,Palette.argb(p.text,255));
        Canvas.centered(g,CAPTION,width/2,32,Palette.argb(p.muted,255));
        super.extractRenderState(g,mouseX,mouseY,delta);
    }
    @Override public void extractBackground(GuiGraphicsExtractor g,int x,int y,float delta) {}
    @Override public void onClose() { minecraft.gui.setScreen(parent); }
}

package dev.meteorpurple.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;

/** Vanilla menu, actions, narration and addon hooks; only the panorama is replaced. */
public final class PurpleTitleScreen extends TitleScreen {
    public PurpleTitleScreen() { super(false); }
    @Override protected void init() {
        super.init();
        addRenderableWidget(new PurpleButton(8,8,70,20,"Themes",() -> minecraft.gui.setScreen(new ThemeGalleryScreen(this))));
    }
    @Override protected void extractPanorama(GuiGraphicsExtractor g,float delta) {
        var a=dev.meteorpurple.modules.Appearance.get();
        if(a!=null && !a.animate()) minecraft.gameRenderer.gameRenderState().optionsRenderState.panoramaSpeed=0;
        // Use Minecraft's cubemap renderer directly; the captured shader lighting is baked in.
        minecraft.gameRenderer.panorama().extractRenderState(g,width,height);
        g.fillGradient(0,0,width,height,0x08080b18,0x28080b18);
    }
}

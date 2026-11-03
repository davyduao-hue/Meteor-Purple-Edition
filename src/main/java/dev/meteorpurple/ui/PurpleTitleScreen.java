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
        Palette selected=Canvas.palette();
        Palette p=selected.light ? dev.meteorpurple.modules.Appearance.Style.Midnight.palette:selected;
        g.fillGradient(0,0,width,height,Palette.argb(p.mantle,255),Palette.argb(p.base,255));
        MeteorMark.draw(g,width,height);
        g.fillGradient(0,height/3,width,height,0x00000000,Palette.argb(p.mantle,200));
    }
}

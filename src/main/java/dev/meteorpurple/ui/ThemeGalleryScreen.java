package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ThemeGalleryScreen extends Screen {
    private final Screen parent;
    private static final Component HEADING=Canvas.label("Choose your atmosphere"),CAPTION=Canvas.label("Live palettes for your home, client UI and HUD");
    public ThemeGalleryScreen(Screen parent) { super(Component.literal("Purple theme gallery")); this.parent=parent; }
    @Override protected void init() {
        int w=Math.min(286,(width-44)/2),left=(width-(w*2+12))/2;
        int step=Math.max(22,Math.min(46,(height-95)/5));
        int top=48;
        Appearance.Style[] styles=Appearance.Style.values();
        for (int i=0;i<styles.length;i++) {
            Appearance.Style style=styles[i];
            PurpleButton card=new PurpleButton(left+(i%2)*(w+12),top+(i/2)*step,w,Math.max(20,step-7),style.title,
                () -> Appearance.get().select(style),style.palette);
            card.setTooltip(net.minecraft.client.gui.components.Tooltip.create(Component.literal(style.subtitle)));
            addRenderableWidget(card);
        }
        addRenderableWidget(new PurpleButton(width/2-80,height-31,160,22,"Done",this::onClose));
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
        Canvas.background(g,width,height); Palette p=Canvas.palette();
        Canvas.centered(g,HEADING,width/2,15,Palette.argb(p.text,255));
        Canvas.centered(g,CAPTION,width/2,30,Palette.argb(p.muted,255));
        super.extractRenderState(g,mouseX,mouseY,delta);
    }
    @Override public void extractBackground(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {}
    @Override public void onClose() {
        if (parent instanceof PurpleHubScreen) minecraft.gui.setScreen(new PurpleHubScreen(meteordevelopment.meteorclient.gui.GuiThemes.get()));
        else minecraft.gui.setScreen(parent);
    }
}

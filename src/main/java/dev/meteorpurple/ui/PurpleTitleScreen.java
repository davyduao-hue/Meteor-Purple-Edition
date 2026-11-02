package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import meteordevelopment.meteorclient.gui.GuiThemes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

public final class PurpleTitleScreen extends Screen {
    private int cardX,cardY,cardW,cardH;
    private static final Component BRAND=Canvas.label("P U R P L E   E D I T I O N"),TITLE=Canvas.label("Meteor."),
        TAGLINE=Canvas.label("Your client. One home."),BUNDLE=Canvas.label("Meteor + Catppuccin included"),
        SHORTCUT=Canvas.label("Right Shift  /  Classic UI"),CLASSIC=Canvas.label("Classic UI is always available."),
        VERSION=Canvas.label("26.2  /  Fabric  /  Purple 1.1.1");
    private Appearance.Style lastStyle;
    private Component styleLabel=Canvas.label("Loading appearance…");
    public PurpleTitleScreen() { super(Component.literal("Meteor Purple Edition")); }
    @Override protected void init() {
        cardW=Math.min(700,width-24); cardH=Math.min(336,height-24);
        cardX=(width-cardW)/2; cardY=(height-cardH)/2;
        int bw=Math.max(120,(cardW-76)/2),bx=cardX+cardW-bw-24;
        int gap=Math.max(24,Math.min(36,(cardH-72)/6));
        int by=cardY+44;
        button(bx,by,bw,"Singleplayer",() -> minecraft.gui.setScreen(new SelectWorldScreen(this)));
        button(bx,by+gap,bw,"Multiplayer",() -> minecraft.gui.setScreen(new JoinMultiplayerScreen(this)));
        button(bx,by+gap*2,bw,"Classic UI",() -> minecraft.gui.setScreen(GuiThemes.get().modulesScreen()));
        button(bx,by+gap*3,bw,"Themes & appearance",() -> minecraft.gui.setScreen(new ThemeGalleryScreen(this)));
        button(bx,by+gap*4,bw,"Minecraft options",() -> minecraft.gui.setScreen(new OptionsScreen(this,minecraft.options,false)));
        button(bx,by+gap*5,bw,"Quit game",minecraft::stop);
    }
    private void button(int x,int y,int w,String title,Runnable action) { addRenderableWidget(new PurpleButton(x,y,w,24,title,action)); }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
        Canvas.background(g,width,height); Palette p=Canvas.palette();
        Canvas.card(g,cardX,cardY,cardW,cardH,p);
        int x=cardX+24,y=cardY+48;
        g.text(font,BRAND,x,y,Palette.argb(p.accent,255),false);
        g.pose().pushMatrix(); g.pose().scale(2f);
        g.text(font,TITLE,x/2,(y+28)/2,Palette.argb(p.text,255),false);
        g.pose().popMatrix();
        g.text(font,TAGLINE,x,y+68,Palette.argb(p.muted,255),false);
        g.text(font,BUNDLE,x,y+104,Palette.argb(p.muted,255),false);
        Appearance a=Appearance.get();
        if(a!=null && lastStyle!=a.style.get()) { lastStyle=a.style.get(); styleLabel=Canvas.label(lastStyle.title); }
        g.text(font,styleLabel,x,y+124,Palette.argb(p.accent,255),false);
        if (cardH>280) {
            g.text(font,SHORTCUT,x,y+172,Palette.argb(p.muted,255),false);
            g.text(font,CLASSIC,x,y+188,Palette.argb(p.muted,255),false);
        }
        g.text(font,VERSION,cardX+24,cardY+cardH-22,Palette.argb(p.muted,255),false);
        super.extractRenderState(g,mouseX,mouseY,delta);
    }
    @Override public void extractBackground(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {}
    @Override public boolean shouldCloseOnEsc() { return false; }
    @Override public boolean isPauseScreen() { return false; }
}

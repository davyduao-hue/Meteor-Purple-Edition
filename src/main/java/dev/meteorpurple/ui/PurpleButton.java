package dev.meteorpurple.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;

public class PurpleButton extends Button {
    private final Palette preview;
    private final ButtonMotion motion=new ButtonMotion();
    public PurpleButton(int x,int y,int w,int h,String title,Runnable action) { this(x,y,w,h,title,action,null); }
    public PurpleButton(int x,int y,int w,int h,String title,Runnable action,Palette preview) {
        super(x,y,w,h,Canvas.label(title),ignored -> action.run(),DEFAULT_NARRATION);
        this.preview=preview;
    }
    @Override public void onPress(net.minecraft.client.input.InputWithModifiers input) { motion.press(); super.onPress(input); }
    @Override protected void extractContents(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
        ButtonPaint.draw(g,this,preview==null ? Canvas.palette():preview,motion);
    }
}

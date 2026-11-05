package dev.meteorpurple.ui;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.WCatppuccinButton;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.utils.render.color.Color;

public final class ZenithButton extends WCatppuccinButton {
    private final ClassicMotion motion=new ClassicMotion();
    private final Color fill=new Color(),edge=new Color();
    public ZenithButton(RichText text,GuiTexture texture) { super(text,texture); }
    public ZenithButton(GuiTexture texture) { super(texture); }
    @Override protected void onPressed(int button) { motion.click(); super.onPressed(button); }
    @Override protected void onRender(GuiRenderer r,double mx,double my,double delta) {
        if(!shouldRender(mouseOver)) return;
        Palette p=Canvas.palette(); double t=motion.update(mouseOver || focused,pressed,delta),s=theme.scale(1);
        double inset=motion.press.value()*s;
        ClassicPaint.mix(fill,p.surface0,p.surface2,t*.8);
        edge.set(p.accent.r,p.accent.g,p.accent.b,(int)(50+t*140));
        ClassicPaint.panel(x+inset,y+inset,width-inset*2,height-inset*2,theme().scale(ThemeDesign.current().controlRadius()),fill,edge);
        if(t>.001) ClassicPaint.rect(x+width/2-(width-20*s)*t/2,y+height-2*s,(width-20*s)*t,2*s,s,p.accent);
        renderContent(r,pad());
    }
}

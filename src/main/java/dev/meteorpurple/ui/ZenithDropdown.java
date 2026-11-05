package dev.meteorpurple.ui;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.input.WCatppuccinDropdown;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

/** Native dropdown root, selection callbacks, keyboard and popup clipping are inherited. */
public final class ZenithDropdown<T> extends WCatppuccinDropdown<T> {
    private final RichText heading;
    private T previous;
    private RichText valueLabel;
    private final ClassicMotion motion=new ClassicMotion();
    private final Color edge=new Color();
    public ZenithDropdown(String title,T[] values,T value) { super(title,values,value); heading=RichText.of(title==null ? "":title); }
    @Override protected void onRender(GuiRenderer r,double mx,double my,double delta) {
        if(valueLabel==null || previous!=value) { previous=value; valueLabel=RichText.of(value.toString()); }
        Palette p=Canvas.palette(); double t=motion.update(mouseOver || focused,expanded,delta),s=theme.scale(1);
        edge.set(p.accent.r,p.accent.g,p.accent.b,(int)(55+t*125));
        ClassicPaint.panel(x,y,width,height,theme().scale(ThemeDesign.current().controlRadius()),p.surface0,edge);
        double tx=x+pad(),ty=y+(height-theme.textHeight())/2;
        if(!heading.getPlainText().isEmpty()) { renderer().text(heading,tx,ty,p.muted); tx+=theme().textWidth(heading)+theme.scale(12); }
        renderer().text(valueLabel,tx,ty,p.accent);
        double size=theme.textHeight();
        r.rotatedQuad(x+width-pad()-size,y+(height-size)/2,size,size,180*(1-motion.active.value()),ThemeIcons.get(ThemeIcons.Icon.ARROW),ClassicPaint.WHITE);
    }
}

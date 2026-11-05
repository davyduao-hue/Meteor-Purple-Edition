package dev.meteorpurple.ui;

import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.WCatppuccinModule;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;

/** Native WPressable handlers remain inherited: left toggles, right opens the actual module. */
public final class ZenithModule extends WCatppuccinModule {
    private final Module module;
    private final RichText label;
    private double labelWidth;
    private final ClassicMotion motion=new ClassicMotion();
    private final Color fill=new Color(),text=new Color(),track=new Color();
    public ZenithModule(Module module,String label) { super(module,label); this.module=module; this.label=RichText.of(label); }
    @Override public void init() { motion.active.reset(module.isActive() ? 1:0); }
    @Override protected void onCalculateSize() {
        labelWidth=theme().textWidth(label); width=labelWidth+theme.scale(56); height=theme.scale(31);
    }
    @Override protected void onPressed(int button) { motion.click(); super.onPressed(button); }
    @Override protected void onRender(GuiRenderer r,double mx,double my,double delta) {
        Palette p=Canvas.palette(); double s=theme.scale(1),t=motion.update(mouseOver,module.isActive(),delta);
        double on=Math.max(0,Math.min(1,motion.active.value())),press=motion.press.value();
        fill.set(p.accent.r,p.accent.g,p.accent.b,(int)(t*20+on*ThemeDesign.current().rowTint()));
        ClassicPaint.rect(x+2*s+press*s,y+2*s,width-4*s-press*s*2,height-4*s,theme().scale(ThemeDesign.current().controlRadius()),fill);
        if(on>.001) ClassicPaint.rect(x+3*s,y+height*.28,2*s,height*.44*on,s,p.accent);
        double tx=x+theme.scale(13)+t*s*2;
        ClassicPaint.mix(text,p.text,p.accent,on*.20);
        renderer().text(label,tx,y+(height-theme.textHeight())/2,text);
        // A small switch makes module state immediately clear without moving text or hitboxes.
        double tw=theme.scale(19),th=theme.scale(10),sx=x+width-tw-theme.scale(9),sy=y+(height-th)/2;
        ClassicPaint.mix(track,p.surface2,p.accent,on*.65);
        ClassicPaint.rect(sx,sy,tw,th,th/2,track);
        double knob=theme.scale(6),kx=sx+theme.scale(2)+(tw-knob-theme.scale(4))*on;
        ClassicPaint.rect(kx,sy+(th-knob)/2,knob,knob,knob/2,on>.5 ? p.base:p.muted);
    }
}

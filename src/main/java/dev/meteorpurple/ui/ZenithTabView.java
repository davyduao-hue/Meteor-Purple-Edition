package dev.meteorpurple.ui;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.container.WCatppuccinTabView;
import me.pindour.catppuccin.gui.widgets.tabs.CatppuccinTab;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import java.util.List;

public final class ZenithTabView extends WCatppuccinTabView {
    public ZenithTabView(List<CatppuccinTab> tabs,CatppuccinTab initial) { super(tabs,initial); }
    @Override protected WHeader createHeader() { return new ZenithHeader(); }
    @Override protected WTabButton createTabButton(CatppuccinTab tab) { return new ZenithTab(tab); }
    private final class ZenithHeader extends WCatppuccinHeader {
        private final Spring ix=new Spring(0),iw=new Spring(0);
        private boolean first=true;
        @Override protected void onTabChange(CatppuccinTab tab) { }
        @Override protected void onRender(GuiRenderer r,double mx,double my,double delta) {
            var p=Canvas.palette(); int i=tabs.indexOf(activeTab);
            if(i<0 || i>=cells.size()) return;
            var b=cells.get(i).widget(); float tx=(float)(b.x-x+4),tw=(float)(b.width-8);
            if(first || !MotionPrefs.ui()) { ix.reset(tx); iw.reset(tw); first=false; }
            else { ix.update(tx,(float)delta,19); iw.update(tw,(float)delta,19); }
            ClassicPaint.rect(x+ix.value(),y+4,iw.value(),height-8,theme().scale(ThemeDesign.current().controlRadius()),ClassicPaint.row());
            ClassicPaint.rect(x+ix.value()+10,y+height-3,Math.max(1,iw.value()-20),2,1,p.accent);
        }
    }
    private final class ZenithTab extends WCatppuccinTabButton {
        private final RichText label;
        private final ClassicMotion motion=new ClassicMotion();
        private double labelWidth;
        private boolean iconOnly;
        ZenithTab(CatppuccinTab tab) { super(tab); label=RichText.of(tab.name()); tooltip=tab.name(); }
        @Override protected void onCalculateSize() {
            labelWidth=theme().textWidth(label);
            double cap=Math.max(theme.scale(32),(Utils.getWindowWidth()-theme.scale(36))/(double)Math.max(1,tabs.size()));
            double preferred=labelWidth+theme.textHeight()+theme.scale(28);
            width=Math.min(cap,preferred); height=theme.scale(42);
            iconOnly=width<preferred-theme.scale(6);
        }
        @Override public double contentWidth() { return Math.max(1,width-theme.scale(20)); }
        @Override protected void onRender(GuiRenderer r,double mx,double my,double delta) {
            var p=Canvas.palette(); double t=motion.update(mouseOver,isTabActive(tab),delta),size=theme.textHeight();
            if(t>0) ClassicPaint.rect(x+4,y+4,width-8,height-8,theme().scale(7),ClassicPaint.faint());
            ThemeIcons.Icon icon=switch(tab.name().toLowerCase(java.util.Locale.ROOT)) {
                case "modules" -> ThemeIcons.Icon.CUBE; case "config" -> ThemeIcons.Icon.SETTING; case "gui" -> ThemeIcons.Icon.BRUSH;
                case "hud" -> ThemeIcons.Icon.GRID; case "friends" -> ThemeIcons.Icon.PEOPLE; case "macros" -> ThemeIcons.Icon.MOUSE;
                case "profiles" -> ThemeIcons.Icon.PERSON; default -> ThemeIcons.Icon.MOVEMENT;
            };
            double xx=iconOnly ? x+(width-size)/2:x+(width-size-theme.scale(6)-labelWidth)/2;
            r.quad(xx,y+(height-size)/2,size,size,ThemeIcons.get(icon),ClassicPaint.WHITE);
            if(!iconOnly) renderer().text(label,xx+size+theme.scale(6),y+(height-theme.textHeight())/2,isTabActive(tab) ? p.text:p.muted);
        }
    }
}

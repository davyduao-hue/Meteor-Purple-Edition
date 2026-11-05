package qa;
import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import meteordevelopment.meteorclient.gui.*;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.*;
import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.*;
import java.util.ArrayList;

final class ClientUiAudit {
    static void run(Minecraft mc) {
        Appearance a=Appearance.get(); var parent=mc.gui.screen(); var savedFont=Config.get().font.get();
        a.customClientUi.set(false); a.applyPending(); require(GuiThemes.get().name.equals("Meteor"),"native client UI fallback");
        a.customClientUi.set(true); a.applyPending();
        var t=(CatppuccinGuiTheme)GuiThemes.get();
        require(t.richTextRenderer().fontFace==UiFont.FACE,"bundled theme typeface");
        require(savedFont==Config.get().font.get(),"global font choice unchanged");
        a.roundedTypeface.set(false); require(t.richTextRenderer().fontFace==savedFont,"configured typeface fallback");
        a.roundedTypeface.set(true); require(t.richTextRenderer().fontFace==UiFont.FACE,"bundled font restored");
        var row=t.module(a); require(row instanceof ZenithModule,"custom module row"); row.init();
        boolean before=a.isActive(); click(row,0); require(a.isActive()!=before,"module native left action"); click(row,0); require(a.isActive()==before,"module state restored");
        click(row,1); require(mc.gui.screen()!=parent,"module native right settings action"); mc.gui.setScreen(parent);
        var checkbox=t.checkbox(false); require(checkbox instanceof ZenithCheckbox,"switch widget"); checkbox.init(); click(checkbox,0); require(checkbox.checked,"switch native input");
        var slider=t.slider(5,0,10); require(slider instanceof ZenithSlider,"slider widget"); slider.set(8); require(slider.get()==8,"exact native slider value");
        var dropdown=t.dropdown(Appearance.Motion.values(),Appearance.Motion.Gentle); require(dropdown instanceof ZenithDropdown,"dropdown widget");
        dropdown.init(); dropdown.set(Appearance.Motion.Arcade); require(dropdown.get()==Appearance.Motion.Arcade,"native dropdown selection");
        var button=t.button("Audit"); button.init(); int[] calls={0}; button.action=() -> calls[0]++; click(button,0); require(calls[0]==1,"single native button action");
        require(ThemeIcons.ready(),"theme atlases packed");
        for(var style:Appearance.Style.values()) {
            var icon=ThemeIcons.get(style,ThemeIcons.Icon.SWORD).get(20,20);
            require(icon.x2>icon.x1 && icon.y2>icon.y1,"packed icon UV "+style.name());
        }
        a.reducedMotion.set(true);
        var animation=new me.pindour.catppuccin.api.animation.Animation(me.pindour.catppuccin.api.animation.Easing.BACK_OUT,500);
        animation.start(); require(animation.getProgress()==1 && !animation.isRunning(),"all classic animations obey reduced motion");
        a.reducedMotion.set(false);
        System.out.println("PURPLE_UI_AUDIT PASS: native actions, factories, fonts, ten icon families and motion overrides");
    }
    static void layout(Minecraft mc) {
        if(!(mc.gui.screen() instanceof WidgetScreen screen) || !Appearance.get().customClientUi.get()) return;
        try {
            var field=WidgetScreen.class.getDeclaredField("root"); field.setAccessible(true);
            ArrayList<WWindow> windows=new ArrayList<>(); collect((WWidget)field.get(screen),windows);
            for(var w:windows) if(w.id!=null) {
                require(w.x>=0 && w.x+w.width<=mc.getWindow().getWidth()+1,"category horizontal bounds: "+w.id);
                require(w.y>=0 && w.y+w.height<=mc.getWindow().getHeight()+1,"category vertical bounds: "+w.id);
            }
            require(windows.size()>=7,"all categories reachable");
        } catch(ReflectiveOperationException e) { throw new RuntimeException(e); }
    }
    private static void collect(WWidget w,ArrayList<WWindow> out) {
        if(w instanceof WWindow window) out.add(window);
        if(w instanceof WContainer container) for(var cell:container.cells) collect(cell.widget(),out);
    }
    private static void click(WWidget w,int button) {
        w.mouseOver=true; var event=new MouseButtonEvent(w.x+2,w.y+2,new MouseButtonInfo(button,0));
        w.onMouseClicked(event,false); w.onMouseReleased(event);
    }
    private static void require(boolean ok,String message) { if(!ok) throw new AssertionError(message); }
}

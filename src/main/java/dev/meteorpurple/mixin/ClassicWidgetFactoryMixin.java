package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.api.text.RichText;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.widgets.pressable.WCatppuccinButton;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.*;
import meteordevelopment.meteorclient.gui.widgets.input.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import me.pindour.catppuccin.gui.widgets.tabs.*;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Theme factories are the integration boundary; addon settings remain actual native widgets. */
@Mixin(value=CatppuccinGuiTheme.class,remap=false)
public abstract class ClassicWidgetFactoryMixin {
    @Unique private <T extends WWidget> T zenith$withTheme(T widget) { widget.theme=(GuiTheme)(Object)this; return widget; }
    @Inject(method="module(Lmeteordevelopment/meteorclient/systems/modules/Module;Ljava/lang/String;)Lmeteordevelopment/meteorclient/gui/widgets/WWidget;",at=@At("HEAD"),cancellable=true)
    private void zenith$module(Module m,String title,CallbackInfoReturnable<WWidget> ci) { if(ClassicPaint.enabled()) ci.setReturnValue(zenith$withTheme(new ZenithModule(m,title))); }
    @Inject(method="button(Lme/pindour/catppuccin/api/text/RichText;Lmeteordevelopment/meteorclient/gui/renderer/packer/GuiTexture;)Lme/pindour/catppuccin/gui/themes/catppuccin/widgets/pressable/WCatppuccinButton;",at=@At("HEAD"),cancellable=true)
    private void zenith$button(RichText title,GuiTexture texture,CallbackInfoReturnable<WCatppuccinButton> ci) { if(ClassicPaint.enabled()) ci.setReturnValue(zenith$withTheme(new ZenithButton(title,texture))); }
    @Inject(method="button(Lmeteordevelopment/meteorclient/gui/renderer/packer/GuiTexture;)Lmeteordevelopment/meteorclient/gui/widgets/pressable/WButton;",at=@At("HEAD"),cancellable=true)
    private void zenith$iconButton(GuiTexture texture,CallbackInfoReturnable<WButton> ci) { if(ClassicPaint.enabled()) ci.setReturnValue(zenith$withTheme(new ZenithButton(texture))); }
    @Inject(method="checkbox",at=@At("HEAD"),cancellable=true)
    private void zenith$checkbox(boolean checked,CallbackInfoReturnable<WCheckbox> ci) { if(ClassicPaint.enabled()) ci.setReturnValue(zenith$withTheme(new ZenithCheckbox(checked))); }
    @Inject(method="slider",at=@At("HEAD"),cancellable=true)
    private void zenith$slider(double value,double min,double max,CallbackInfoReturnable<WSlider> ci) { if(ClassicPaint.enabled()) ci.setReturnValue(zenith$withTheme(new ZenithSlider(value,min,max))); }
    @Inject(method="dropdown(Ljava/lang/String;[Ljava/lang/Object;Ljava/lang/Object;)Lmeteordevelopment/meteorclient/gui/widgets/input/WDropdown;",at=@At("HEAD"),cancellable=true)
    private <T> void zenith$dropdown(String title,T[] values,T value,CallbackInfoReturnable<WDropdown<T>> ci) { if(ClassicPaint.enabled()) ci.setReturnValue(zenith$withTheme(new ZenithDropdown<>(title,values,value))); }
    @Inject(method="tabView(Ljava/util/List;Lme/pindour/catppuccin/gui/widgets/tabs/CatppuccinTab;)Lme/pindour/catppuccin/gui/widgets/tabs/WTabView;",at=@At("HEAD"),cancellable=true)
    private void zenith$tabs(List<CatppuccinTab> tabs,CatppuccinTab initial,CallbackInfoReturnable<WTabView> ci) { if(ClassicPaint.enabled()) ci.setReturnValue(zenith$withTheme(new ZenithTabView(tabs,initial))); }
}

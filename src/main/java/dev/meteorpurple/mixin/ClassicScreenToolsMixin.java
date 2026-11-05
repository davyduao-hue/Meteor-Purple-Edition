package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.gui.screens.*;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static meteordevelopment.meteorclient.MeteorClient.mc;
@Mixin(value=CatppuccinModulesScreen.class,remap=false)
public abstract class ClassicScreenToolsMixin {
    @Inject(method="initWidgets",at=@At("TAIL"))
    private void zenith$tools(CallbackInfo ci) {
        if(!ClassicPaint.enabled()) return;
        var screen=(CatppuccinModulesScreen)(Object)this; var theme=(CatppuccinGuiTheme)meteordevelopment.meteorclient.gui.GuiThemes.get();
        var dock=screen.add(theme.horizontalList()).bottom().right().pad(14).widget();
        dock.add(theme.button("Search")).widget().action=() -> mc.gui.setScreen(new CatppuccinSearchScreen(theme));
        dock.add(theme.button("Themes")).widget().action=() -> mc.gui.setScreen(new ThemeGalleryScreen(screen));
        dock.add(theme.button("Motion")).widget().action=() -> mc.gui.setScreen(new MotionScreen(screen));
    }
}

package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import dev.meteorpurple.modules.Appearance;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.renderer.text.RichTextRenderer;
import meteordevelopment.meteorclient.systems.config.Config;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=CatppuccinGuiTheme.class,remap=false)
public abstract class ClassicFontMixin {
    @Shadow private RichTextRenderer textRenderer;
    @Unique private boolean zenith$fontFailed;
    @Inject(method="richTextRenderer",at=@At("HEAD"),cancellable=true)
    private void zenith$font(CallbackInfoReturnable<RichTextRenderer> ci) {
        if(zenith$fontFailed || Appearance.get()==null) return;
        boolean enabled=ClassicPaint.enabled() && Appearance.get().roundedTypeface.get();
        boolean bundled=textRenderer!=null && textRenderer.fontFace==UiFont.FACE;
        if(!enabled && !bundled) return;
        try {
            if(enabled && !bundled) {
                UiFont.registerFamily(); ((CatppuccinGuiTheme)(Object)this).setTextRenderer(new RichTextRenderer(UiFont.FACE));
            } else if(!enabled) ((CatppuccinGuiTheme)(Object)this).setTextRenderer(new RichTextRenderer(Config.get().font.get()));
            ci.setReturnValue(textRenderer);
        } catch(Exception e) { zenith$fontFailed=true; org.slf4j.LoggerFactory.getLogger("Zenith").warn("Using the configured font after a Zenith font load failure",e); }
    }
}

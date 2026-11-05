package dev.meteorpurple.mixin;

import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.Palette;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Provides cached preset colors without mutating upstream palette objects. */
@Mixin(value = CatppuccinGuiTheme.class, remap = false)
public abstract class GlassPaletteMixin {
    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void purple$glassPalette(CatppuccinColor key, CallbackInfoReturnable<Color> cir) {
        Appearance appearance = Appearance.get();
        if (appearance == null || !appearance.customClientUi.get()) return;
        Palette palette = appearance.palette();
        Color color = switch (key) {
            case Base -> palette.base;
            case Mantle -> palette.mantle;
            case Crust -> palette.crust;
            case Surface0 -> palette.surface0;
            case Surface1 -> palette.surface1;
            case Surface2 -> palette.surface2;
            case Accent, Lavender -> palette.accent;
            case Text -> palette.text;
            case Subtext0, Subtext1 -> palette.muted;
            default -> null;
        };
        if (color != null) cir.setReturnValue(color);
    }
}

package dev.meteorpurple.mixin;

import dev.meteorpurple.modules.Appearance;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import me.pindour.catppuccin.gui.themes.catppuccin.colors.CatppuccinColor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Tints only Glass surfaces, preserving the upstream palette objects. */
@Mixin(value = CatppuccinGuiTheme.class, remap = false)
public abstract class GlassPaletteMixin {
    private static final Color PURPLE_BASE = new Color(34, 22, 51);
    private static final Color PURPLE_MANTLE = new Color(27, 17, 43);
    private static final Color PURPLE_CRUST = new Color(20, 13, 33);
    private static final Color PURPLE_SURFACE_0 = new Color(52, 35, 73);
    private static final Color PURPLE_SURFACE_1 = new Color(67, 45, 91);
    private static final Color PURPLE_SURFACE_2 = new Color(81, 56, 109);

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void purple$glassPalette(CatppuccinColor key, CallbackInfoReturnable<Color> cir) {
        if (Modules.get() == null) return;
        Appearance appearance = Modules.get().get(Appearance.class);
        if (appearance == null || appearance.style.get() != Appearance.Style.Glass) return;
        Color color = switch (key) {
            case Base -> PURPLE_BASE;
            case Mantle -> PURPLE_MANTLE;
            case Crust -> PURPLE_CRUST;
            case Surface0 -> PURPLE_SURFACE_0;
            case Surface1 -> PURPLE_SURFACE_1;
            case Surface2 -> PURPLE_SURFACE_2;
            default -> null;
        };
        if (color != null) cir.setReturnValue(color);
    }
}

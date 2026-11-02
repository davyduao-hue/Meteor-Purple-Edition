package dev.meteorpurple.mixin;

import dev.meteorpurple.modules.Appearance;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Uses Minecraft's own frame blur; never edits or saves global video options. */
@Mixin(value = WidgetScreen.class, remap = false)
public abstract class GlassBackgroundMixin {
    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void purple$glassBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!(GuiThemes.get() instanceof CatppuccinGuiTheme) || Modules.get() == null) return;
        Appearance appearance = Modules.get().get(Appearance.class);
        if (appearance == null || !appearance.glassBlurEnabled()) return;
        mc.gameRenderer.gameRenderState().optionsRenderState.menuBackgroundBlurriness = appearance.blurRadius.get();
        graphics.blurBeforeThisStratum();
    }
}

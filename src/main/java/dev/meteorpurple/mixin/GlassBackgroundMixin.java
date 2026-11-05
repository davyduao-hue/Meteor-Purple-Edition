package dev.meteorpurple.mixin;

import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.ui.*;
import me.pindour.catppuccin.gui.themes.catppuccin.CatppuccinGuiTheme;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Uses Minecraft's own frame blur; never edits or saves global video options. */
@Mixin(value = WidgetScreen.class, remap = false)
public abstract class GlassBackgroundMixin {
    @Shadow private boolean closing;
    @Shadow public double animProgress;
    @Unique private static final net.minecraft.network.chat.Component zenith$brand=Canvas.label("Zenith Client");
    @Unique private final long zenith$opened=System.nanoTime();
    @Inject(method="renderCustom",at=@At("HEAD"))
    private void zenith$noFade(GuiGraphicsExtractor g,int x,int y,float delta,CallbackInfo ci) {
        if(ClassicPaint.enabled() && !MotionPrefs.ui()) animProgress=closing ? 0:1;
    }
    @Inject(method="extractBackground",at=@At("HEAD"),cancellable=true)
    private void zenith$backdrop(GuiGraphicsExtractor g,int x,int y,float delta,CallbackInfo ci) {
        if(!ClassicPaint.enabled() || !(GuiThemes.get() instanceof CatppuccinGuiTheme)) return;
        Palette p=Canvas.palette();
        if(mc.level==null) {
            Canvas.background(g,g.guiWidth(),g.guiHeight());
            if(!MotionPrefs.lowCost()) RadialPaint.glow(g,g.guiWidth()*.48f,g.guiHeight()*.22f,g.guiWidth()*.7f,g.guiHeight()*.8f,Palette.argb(p.accent,p.light ? 16:24));
            ci.cancel();
        } else g.fill(0,0,g.guiWidth(),g.guiHeight(),Palette.argb(p.mantle,75));
        float time=(System.nanoTime()-zenith$opened)/1_000_000_000f;
        float bob=MotionPrefs.ui() ? (float)Math.sin(time*2)*.6f:0;
        MeteorMark.drawAt(g,19,g.guiHeight()-17+bob,18,255,0,false);
        g.text(mc.font,zenith$brand,34,g.guiHeight()-21,Palette.argb(p.muted,255),false);
    }
    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void purple$glassBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!(GuiThemes.get() instanceof CatppuccinGuiTheme) || Modules.get() == null) return;
        Appearance appearance = Appearance.get();
        if (appearance == null || !appearance.glassBlurEnabled()) return;
        mc.gameRenderer.gameRenderState().optionsRenderState.menuBackgroundBlurriness = appearance.blurRadius.get();
        graphics.blurBeforeThisStratum();
    }
}


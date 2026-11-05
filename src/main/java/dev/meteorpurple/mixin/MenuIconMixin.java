package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.*;
import dev.meteorpurple.modules.Appearance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SpriteIconButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(SpriteIconButton.class)
public abstract class MenuIconMixin {
    @Inject(method="extractSprite",at=@At("HEAD"),cancellable=true)
    private void zenith$menuIcon(GuiGraphicsExtractor g,int x,int y,CallbackInfo ci) {
        if(!(Minecraft.getInstance().gui.screen() instanceof PurpleTitleScreen) || Appearance.get()==null) return;
        var b=(SpriteIconButton)(Object)this; String title=b.getMessage().getString().toLowerCase(java.util.Locale.ROOT);
        ThemeIcons.Icon icon=title.contains("language") ? ThemeIcons.Icon.GLOBE:title.contains("accessibility") ? ThemeIcons.Icon.ACCESSIBILITY:null;
        if(icon==null) return;
        ThemeIcons.draw(g,Appearance.get().style.get(),icon,b.getX()+2,b.getY()+2,b.getHeight()-4,b.active ? 255:100); ci.cancel();
    }
}

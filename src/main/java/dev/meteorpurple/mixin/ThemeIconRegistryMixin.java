package dev.meteorpurple.mixin;
import dev.meteorpurple.ui.ThemeIcons;
import dev.meteorpurple.ui.ThemeIcons.Icon;
import me.pindour.catppuccin.api.icons.CatppuccinIcons;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=CatppuccinIcons.class,remap=false)
public abstract class ThemeIconRegistryMixin {
    @Inject(method="getCategoryIcon",at=@At("HEAD"),cancellable=true)
    private static void zenith$category(String name,CallbackInfoReturnable<GuiTexture> ci) {
        if(!ThemeIcons.active()) return;
        Icon icon=switch(name) {
            case "Combat" -> Icon.SWORD; case "Player" -> Icon.PERSON; case "Movement" -> Icon.MOVEMENT;
            case "Render" -> Icon.EYE; case "World" -> Icon.CUBE; case "Misc" -> Icon.SETTING;
            case "Purple" -> Icon.LOGO; case "Glazed" -> Icon.MOVEMENT; default -> null;
        };
        if(icon!=null) ci.setReturnValue(ThemeIcons.get(icon));
    }
    @Inject(method="getTabIcon",at=@At("HEAD"),cancellable=true)
    private static void zenith$tab(Class<? extends Tab> tab,CallbackInfoReturnable<GuiTexture> ci) {
        if(!ThemeIcons.active()) return;
        Icon icon=switch(tab.getSimpleName()) {
            case "ModulesTab" -> Icon.CUBE; case "ConfigTab" -> Icon.SETTING; case "GuiTab" -> Icon.BRUSH;
            case "HudTab" -> Icon.GRID; case "FriendsTab" -> Icon.PEOPLE; case "MacrosTab" -> Icon.MOUSE;
            case "ProfilesTab" -> Icon.PERSON; case "PathManagerTab" -> Icon.MOVEMENT; default -> null;
        };
        if(icon!=null) ci.setReturnValue(ThemeIcons.get(icon));
    }
}

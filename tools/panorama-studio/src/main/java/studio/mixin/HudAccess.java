package studio.mixin;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(Hud.class) public interface HudAccess { @Accessor("isHidden") void zenith$hidden(boolean hidden); }

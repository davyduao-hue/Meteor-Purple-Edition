package dev.meteorpurple.hud;

import dev.meteorpurple.PurpleAddon;
import dev.meteorpurple.modules.Appearance;
import meteordevelopment.meteorclient.mixin.MinecraftAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;

/** Independent, movable elements; Meteor owns anchoring, editing and persistence. */
public final class PurpleHud extends HudElement {
    public enum Kind { Watermark, Fps, Ping, Coordinates, Modules, Armor, Effects, Memory, Server }
    private static final Map<Kind, HudElementInfo<PurpleHud>> INFOS = new EnumMap<>(Kind.class);
    public static HudElementInfo<PurpleHud> info(Kind kind) {
        return INFOS.computeIfAbsent(kind, k -> new HudElementInfo<>(PurpleAddon.HUD,
            "purple-" + k.name().toLowerCase(Locale.ROOT), "Purple " + k.name(),
            "Theme-synchronized " + k.name().toLowerCase(Locale.ROOT) + ". Drag in Meteor's HUD editor.", () -> new PurpleHud(k)));
    }

    public static void installLayout() {
        int row = 12;
        for (Kind kind : Kind.values()) {
            boolean exists = false;
            for (HudElement element : Hud.get()) if (element.info == info(kind)) { exists = true; break; }
            if (!exists) Hud.get().add(info(kind), 12, row);
            row += switch (kind) { case Modules -> 210; case Armor -> 130; case Effects -> 130; default -> 34; };
        }
        Hud.get().active = true;
    }

    private final Kind kind;
    private final ArrayList<String> lines = new ArrayList<>();
    private int refresh;
    private final Color bg = new Color(30, 30, 46, 210);
    private final SettingGroup general = settings.getDefaultGroup();
    private final Setting<Double> scale = general.add(new DoubleSetting.Builder().name("scale")
        .description("Text scale for this movable element.").defaultValue(1).range(.5, 3).sliderRange(.5, 3).build());
    private final Setting<Double> opacity = general.add(new DoubleSetting.Builder().name("opacity")
        .description("Panel opacity.").defaultValue(.82).range(0, 1).sliderRange(0, 1).build());
    private final Setting<Boolean> background = general.add(new BoolSetting.Builder().name("background")
        .description("Draw a panel behind this element.").defaultValue(true).build());
    private final Setting<Boolean> synchronize = general.add(new BoolSetting.Builder().name("sync-theme")
        .description("Use the selected Purple appearance colors.").defaultValue(true).build());
    private final Setting<SettingColor> customBackground = general.add(new ColorSetting.Builder().name("background-color")
        .description("Custom panel color when theme synchronization is disabled.")
        .defaultValue(new SettingColor(30, 30, 46)).visible(() -> !synchronize.get()).build());
    private final Setting<SettingColor> customAccent = general.add(new ColorSetting.Builder().name("accent-color")
        .description("Custom text and edge color when theme synchronization is disabled.")
        .defaultValue(new SettingColor(180, 190, 254)).visible(() -> !synchronize.get()).build());
    private final Setting<Integer> maxRows = general.add(new IntSetting.Builder().name("maximum-rows")
        .description("Bound list height and refresh work.").defaultValue(8).range(1, 64).sliderRange(1, 32).build());

    private PurpleHud(Kind kind) { super(info(kind)); this.kind = kind; }

    @Override public void tick(HudRenderer renderer) {
        if (refresh-- > 0) return;
        refresh = 4; // refresh text at 4 Hz, never allocate the data model per frame
        updateLines();
    }

    private void updateLines() {
        lines.clear();
        switch (kind) {
            case Watermark -> lines.add("METEOR / PURPLE EDITION");
            case Fps -> lines.add(MinecraftAccessor.meteor$getFps() + " FPS");
            case Ping -> {
                var player = mc.player;
                var connection = mc.getConnection();
                var entry = player == null || connection == null ? null : connection.getPlayerInfo(player.getUUID());
                lines.add(entry == null ? "Ping: offline" : entry.getLatency() + " ms");
            }
            case Coordinates -> lines.add(mc.player == null ? "XYZ: offline" : String.format(Locale.ROOT,
                "XYZ  %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
            case Modules -> {
                for (Module module : Modules.get().getActive()) {
                    if (lines.size() >= maxRows.get()) break;
                    lines.add(module.title);
                }
                if (lines.isEmpty()) lines.add("No active modules");
            }
            case Armor -> {
                if (mc.player == null) { lines.add("Armor: offline"); break; }
                for (EquipmentSlot slot : ARMOR) {
                    ItemStack item = mc.player.getItemBySlot(slot);
                    lines.add(item.isEmpty() ? slot.getName() + ": empty" : item.getHoverName().getString()
                        + (item.isDamageableItem() ? " " + (item.getMaxDamage() - item.getDamageValue()) + "/" + item.getMaxDamage() : ""));
                }
            }
            case Effects -> {
                if (mc.player != null) for (MobEffectInstance effect : mc.player.getActiveEffects()) {
                    if (lines.size() >= maxRows.get()) break;
                    int seconds = Math.max(0, effect.getDuration() / 20);
                    lines.add(effect.getEffect().value().getDisplayName().getString() + " " + (effect.getAmplifier() + 1)
                        + (effect.isInfiniteDuration() ? " ∞" : String.format(Locale.ROOT, " %d:%02d", seconds / 60, seconds % 60)));
                }
                if (lines.isEmpty()) lines.add("No active effects");
            }
            case Memory -> {
                Runtime runtime = Runtime.getRuntime();
                lines.add("RAM " + (runtime.totalMemory() - runtime.freeMemory()) / 1048576 + " / " + runtime.maxMemory() / 1048576 + " MiB");
            }
            case Server -> lines.add(mc.getCurrentServer() == null ? "Singleplayer / offline" : mc.getCurrentServer().name);
        }
    }

    private static final EquipmentSlot[] ARMOR = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    @Override public void render(HudRenderer renderer) {
        if (lines.isEmpty()) updateLines();
        double lineHeight = renderer.textHeight(false, scale.get()) + 3;
        double width = 0;
        for (String line : lines) width = Math.max(width, renderer.textWidth(line, false, scale.get()));
        setSize(width + 18, lines.size() * lineHeight + 12);
        Appearance appearance = Modules.get().get(Appearance.class);
        Color accent = synchronize.get() ? appearance.accent() : customAccent.get();
        Color base = synchronize.get() ? appearance.background() : customBackground.get();
        bg.set(base.r, base.g, base.b, (int) (255 * opacity.get()));
        if (background.get()) {
            if (appearance.style.get() == Appearance.Style.Glass && !appearance.performance.get()) {
                renderer.quad(x, y, getWidth(), getHeight(), bg, bg, GLASS_END, GLASS_END);
            } else renderer.quad(x, y, getWidth(), getHeight(), bg);
            renderer.quad(x, y, 2, getHeight(), accent);
        }
        double rowY = y + 6;
        for (String line : lines) {
            renderer.text(line, x + 9, rowY, accent, false, scale.get());
            rowY += lineHeight;
        }
    }
    private final Color GLASS_END = new Color(55, 28, 88, 150);
}

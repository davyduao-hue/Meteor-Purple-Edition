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
    public enum Kind { Watermark, Fps, Ping, Coordinates, Modules, Armor, Effects, Memory, Server, Performance }
    private static final Map<Kind, HudElementInfo<PurpleHud>> INFOS = new EnumMap<>(Kind.class);
    public static HudElementInfo<PurpleHud> info(Kind kind) {
        return INFOS.computeIfAbsent(kind, k -> new HudElementInfo<>(PurpleAddon.HUD,
            "purple-" + k.name().toLowerCase(Locale.ROOT), k==Kind.Watermark ? "Zenith Client":k==Kind.Performance ? "Zenith Performance":"Purple " + k.name(),
            "Theme-synchronized " + k.name().toLowerCase(Locale.ROOT) + ". Drag in Meteor's HUD editor.", () -> new PurpleHud(k)));
    }

    /** Add only missing bars. Never clear, move or re-enable a user's existing elements. */
    public static void installStarterLayout() {
        addMissing(Kind.Watermark,12,12,YAnchor.Top);
        addMissing(Kind.Performance,12,-12,YAnchor.Bottom);
        Hud.get().active=true;
    }
    private static void addMissing(Kind kind,int x,int y,YAnchor anchor) {
        for(HudElement element:Hud.get()) if(element.info==info(kind)) return;
        Hud.get().add(info(kind),x,y,XAnchor.Left,anchor);
    }

    private final Kind kind;
    private final ArrayList<String> lines = new ArrayList<>();
    private int refresh;
    private boolean measurementDirty=true;
    private double measuredScale=-1,lineHeight;
    private double frameSeconds;
    private int frameSamples;
    private double averageFrameMs;
    private final Color bg = new Color(30, 30, 46, 210);
    private final SettingGroup general = settings.getDefaultGroup();
    private final Setting<Double> scale = general.add(new DoubleSetting.Builder().name("scale")
        .description("Text scale for this movable element.").defaultValue(.85).range(.5, 3).sliderRange(.5, 3).build());
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

    private final Setting<Boolean> showFps=general.add(new BoolSetting.Builder().name("show-fps")
        .description("Show FPS beside Zenith Client.").defaultValue(true).visible(() -> getKind()==Kind.Watermark).build());
    private final Setting<Boolean> showFrame=general.add(new BoolSetting.Builder().name("frame-time")
        .description("Actual average rendered frame interval, sampled locally.").defaultValue(true).visible(() -> getKind()==Kind.Performance).build());
    private final Setting<Boolean> showMemory=general.add(new BoolSetting.Builder().name("heap-memory")
        .description("Used and maximum JVM heap; this is not whole-PC memory usage.").defaultValue(true).visible(() -> getKind()==Kind.Performance).build());
    private final Setting<Boolean> showPing=general.add(new BoolSetting.Builder().name("ping")
        .description("Show connection latency; offline displays a dash.").defaultValue(true).visible(() -> getKind()==Kind.Performance).build());
    private Kind getKind() { return kind; }

    private PurpleHud(Kind kind) { super(info(kind)); this.kind = kind; }

    @Override public void tick(HudRenderer renderer) {
        if (refresh-- > 0) return;
        refresh = Appearance.get().performance.get() ? 9:4;
        updateLines();
    }

    private void updateLines() {
        measurementDirty=true;
        lines.clear();
        switch (kind) {
            case Watermark -> lines.add("Zenith Client"+(showFps.get() ? "  |  "+MinecraftAccessor.meteor$getFps()+" FPS":""));
            case Performance -> {
                if(frameSamples>0) { averageFrameMs=1000*frameSeconds/frameSamples; frameSeconds=0; frameSamples=0; }
                String frame=showFrame.get() ? String.format(Locale.ROOT,"Frame %.1f ms",averageFrameMs):"";
                var player=mc.player; var connection=mc.getConnection();
                var entry=player==null || connection==null ? null:connection.getPlayerInfo(player.getUUID());
                String ping=showPing.get() ? "Ping "+(entry==null ? "offline":entry.getLatency()+" ms"):"";
                if(!frame.isEmpty() || !ping.isEmpty()) lines.add(frame+(!frame.isEmpty() && !ping.isEmpty() ? "  |  ":"")+ping);
                if(showMemory.get()) {
                    Runtime r=Runtime.getRuntime();
                    lines.add("Heap "+(r.totalMemory()-r.freeMemory())/1048576+" / "+r.maxMemory()/1048576+" MiB");
                }
                if(lines.isEmpty()) lines.add("Performance");
            }
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
        if(kind==Kind.Performance && renderer.delta>0 && renderer.delta<5) { frameSeconds+=renderer.delta; frameSamples++; }
        if (lines.isEmpty()) updateLines();
        if(measurementDirty || measuredScale!=scale.get()) {
            measuredScale=scale.get(); lineHeight=renderer.textHeight(false,measuredScale)+3;
            double width=0;
            for(String line:lines) width=Math.max(width,renderer.textWidth(line,false,measuredScale));
            if(kind==Kind.Watermark && showFps.get()) width=Math.max(width,renderer.textWidth("Zenith Client  |  9999 FPS",false,measuredScale));
            setSize(width+22,lines.size()*lineHeight+13); updatePos(); measurementDirty=false;
        }
        Appearance appearance = Appearance.get();
        Color accent = synchronize.get() ? appearance.accent() : customAccent.get();
        Color base = synchronize.get() ? appearance.background() : customBackground.get();
        int alpha = appearance.performance.get() ? 255 : (int) (255 * opacity.get());
        bg.set(base.r, base.g, base.b, alpha);
        if (background.get()) {
            // Tiny cached radius; 13 batched quads, with no blur, shadows or texture pass.
            rounded(renderer,x,y,getWidth(),getHeight(),bg);
            renderer.quad(x+8,y+getHeight()-2,getWidth()-16,2,accent);
        }
        double rowY = y + 6;
        for (String line : lines) {
            renderer.text(line,x+9,rowY,synchronize.get() ? appearance.palette().text:accent,false,measuredScale);
            rowY += lineHeight;
        }
    }
    private static final double[] INSET={3.60,2.03,1.13,.55,.19,.02};
    private static void rounded(HudRenderer r,int x,int y,int w,int h,Color color) {
        r.quad(x,y+6,w,h-12,color);
        for(int row=0;row<6;row++) {
            double inset=INSET[row];
            r.quad(x+inset,y+row,w-inset*2,1,color);
            r.quad(x+inset,y+h-row-1,w-inset*2,1,color);
        }
    }
    @Override public void onFontChanged() { measurementDirty=true; }
}

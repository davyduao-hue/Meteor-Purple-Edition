package dev.meteorpurple.modules;

import dev.meteorpurple.PurpleAddon;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Method;

/** Optional adapter: preserves OpSec's own config, validation and protection engine. */
public final class Privacy extends Module {
    public Privacy() {
        super(PurpleAddon.CATEGORY, "purple-privacy", "OpSec status, settings and notifications inside the purple GUI. This module never disables OpSec's protections.");
        runInMainMenu = true;
    }

    @Override public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        var loader=FabricLoader.getInstance();
        list.add(theme.label("Glazed: "+(loader.isModLoaded("glazed") ? "installed — native Meteor modules":"not installed")));
        list.add(theme.label("ExploitPreventer: "+(loader.isModLoaded("exploitpreventer") ? "installed":"not installed")));
        boolean installed = FabricLoader.getInstance().isModLoaded("opsec");
        list.add(theme.label(installed ? "OpSec installed — official protection engine" : "OpSec absent — install the official 26.2 release"));
        if (!installed) return list;
        try {
            Object config = config();
            if(loader.isModLoaded("exploitpreventer")) {
                try {
                    boolean managed=config.getClass().getField("EXPLOIT_PREVENTER_LOADED").getBoolean(null);
                    list.add(theme.label(managed ? "OpSec compatibility mode active — overlapping protections managed by EP":"OpSec has not enabled EP compatibility mode"));
                } catch(NoSuchFieldException e) {
                    list.add(theme.label("This OpSec build has no recognized EP compatibility mode. Update OpSec."));
                }
            }
            addStatus(list, theme, config, "shouldBlockLocalPackUrls", "Block local pack URLs");
            addStatus(list, theme, config, "isTranslationProtectionEnabled", "Key resolution protection");
            addStatus(list, theme, config, "isMeteorFix", "Meteor compatibility fix");
            WButton open = list.add(theme.button("Open all official OpSec settings")).expandX().widget();
            open.action = () -> {
                try {
                    Class<?> screen = Class.forName("aurick.opsec.mod.config.OpsecConfigScreen");
                    mc.gui.setScreen((Screen) screen.getConstructor(Screen.class).newInstance(mc.gui.screen()));
                } catch (ReflectiveOperationException | LinkageError e) { error("OpSec screen unavailable: %s", root(e)); }
            };
            notificationButton(list, theme, "Toggle chat alerts", "isShowAlerts", "setShowAlerts");
            notificationButton(list, theme, "Toggle toast notifications", "isShowToasts", "setShowToasts");
        } catch (ReflectiveOperationException | LinkageError e) {
            list.add(theme.label("OpSec API differs: " + root(e)));
        }
        return list;
    }

    private void notificationButton(WVerticalList list, GuiTheme theme, String title, String getter, String setter) {
        WButton button = list.add(theme.button(title)).expandX().widget();
        button.action = () -> {
            try {
                Object config = config();
                Object settings = config.getClass().getMethod("getSettings").invoke(config);
                boolean value = !(boolean) settings.getClass().getMethod(getter).invoke(settings);
                settings.getClass().getMethod(setter, boolean.class).invoke(settings, value);
                config.getClass().getMethod("save").invoke(config);
                button.set(title + ": " + (value ? "ON" : "OFF"));
                info("OpSec notification setting saved.");
            } catch (ReflectiveOperationException | LinkageError e) { error("OpSec setting failed: %s", root(e)); }
        };
    }
    private void addStatus(WVerticalList list, GuiTheme theme, Object config, String method, String title) throws ReflectiveOperationException {
        Method getter = config.getClass().getMethod(method);
        list.add(theme.label(title + ": " + ((boolean) getter.invoke(config) ? "ON" : "OFF")));
    }
    private static Object config() throws ReflectiveOperationException {
        return Class.forName("aurick.opsec.mod.config.OpsecConfig").getMethod("getInstance").invoke(null);
    }
    private static String root(Throwable error) {
        while (error.getCause() != null) error = error.getCause();
        return error.getClass().getSimpleName() + ": " + error.getMessage();
    }
}

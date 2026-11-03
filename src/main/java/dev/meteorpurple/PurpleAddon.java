package dev.meteorpurple;

import dev.meteorpurple.hud.PurpleHud;
import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.modules.Privacy;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;

public final class PurpleAddon extends MeteorAddon {
    public static final Category CATEGORY = new Category("Purple");
    public static final HudGroup HUD = new HudGroup("Purple Edition");
    private Appearance appearance;
    private int startupTicks;

    @Override public void onInitialize() {
        appearance = new Appearance();
        Modules.get().add(appearance);
        Modules.get().add(new Privacy());
        for (PurpleHud.Kind kind : PurpleHud.Kind.values()) Hud.get().register(PurpleHud.info(kind));
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    // Apply only after all Meteor addons and persisted settings have initialized.
    @EventHandler private void onTick(TickEvent.Post event) { appearance.applyPending(); if(startupTicks<40 && ++startupTicks==40) appearance.installStarterHudOnce(); }
    @Override public void onRegisterCategories() { Modules.registerCategory(CATEGORY); }
    @Override public String getPackage() { return "dev.meteorpurple"; }
}

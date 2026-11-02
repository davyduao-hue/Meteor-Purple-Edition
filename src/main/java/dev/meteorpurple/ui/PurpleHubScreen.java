package dev.meteorpurple.ui;

import dev.meteorpurple.hud.PurpleHud;
import dev.meteorpurple.modules.Appearance;
import dev.meteorpurple.modules.Privacy;
import meteordevelopment.meteorclient.events.meteor.ActiveModulesChangedEvent;
import meteordevelopment.meteorclient.gui.*;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.WKeybind;
import meteordevelopment.meteorclient.gui.widgets.containers.*;
import meteordevelopment.meteorclient.gui.widgets.input.*;
import meteordevelopment.meteorclient.gui.widgets.pressable.*;
import meteordevelopment.meteorclient.systems.hud.screens.HudEditorScreen;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.Utils.*;

/** One searchable surface, using upstream settings widgets for every installed addon. */
public final class PurpleHubScreen extends WindowScreen {
    private record Entry(Module module,String search) {}
    private final List<Entry> entries;
    private final List<Module> filtered=new ArrayList<>();
    private final Map<Module,WCheckbox> visibleToggles=new IdentityHashMap<>();
    private WView results,details;
    private WVerticalList settingsContainer;
    private WTextBox search;
    private WDropdown<String> category;
    private WCheckbox activeOnly,selectedActive;
    private WKeybind keybind;
    private Module selected;
    private int page;
    private static final int PAGE_SIZE=32;

    public PurpleHubScreen(GuiTheme theme) {
        super(theme,"METEOR / PURPLE EDITION");
        entries=Modules.get().getAll().stream().sorted(Comparator.comparing(m -> m.title))
            .map(m -> new Entry(m,(m.title+" "+m.name+" "+m.description+" "+m.category.name+" "+(m.addon==null ? "":m.addon.name)).toLowerCase(Locale.ROOT))).toList();
        selected=Appearance.get();
    }
    public boolean usesTheme(GuiTheme candidate) { return theme==candidate; }
    @Override public void extractBackground(GuiGraphicsExtractor graphics,int mouseX,int mouseY,float delta) {
        if(mc.level==null) Canvas.background(graphics,width,height);
        else super.extractBackground(graphics,mouseX,mouseY,delta);
    }
    @Override public void initWidgets() {
        var toolbar=add(theme.horizontalList()).expandX().widget();
        button(toolbar,"Themes",() -> mc.gui.setScreen(new ThemeGalleryScreen(this)));
        button(toolbar,"HUD editor",() -> mc.gui.setScreen(new HudEditorScreen(theme)));
        button(toolbar,"Add HUD",PurpleHud::installLayout);
        button(toolbar,"Privacy",() -> select(Modules.get().get(Privacy.class)));
        button(toolbar,"Appearance",() -> select(Appearance.get()));
        button(toolbar,"Classic UI",() -> { Appearance.get().layout.set(Appearance.Layout.Classic); mc.gui.setScreen(GuiThemes.get().modulesScreen()); });

        var navigation=add(theme.horizontalList()).expandX().widget();
        String[] tabNames=new String[Tabs.get().size()+1]; tabNames[0]="More Meteor settings…";
        for (int i=0;i<Tabs.get().size();i++) tabNames[i+1]=Tabs.get().get(i).name;
        var tabs=navigation.add(theme.dropdown(tabNames,tabNames[0])).expandX().widget();
        tabs.action=() -> { for(var tab:Tabs.get()) if(tab.name.equals(tabs.get())) { tab.openScreen(theme); break; } };
        var mode=navigation.add(theme.checkbox(Appearance.get().performance.get())).widget();
        navigation.add(theme.label("Performance mode"));
        mode.action=() -> Appearance.get().performance.set(mode.checked);

        add(theme.horizontalSeparator()).expandX();
        var filters=add(theme.horizontalList()).expandX().widget();
        search=filters.add(theme.textBox("")).minWidth(180).expandX().widget();
        search.tooltip="Search modules, descriptions, categories and addon names";
        List<String> categories=new ArrayList<>(); categories.add("All categories");
        for(var c:Modules.loopCategories()) categories.add(c.name);
        category=filters.add(theme.dropdown(categories.toArray(String[]::new),categories.getFirst())).widget();
        activeOnly=filters.add(theme.checkbox(false)).widget(); filters.add(theme.label("Active"));
        search.action=category.action=activeOnly.action=() -> { page=0; refilter(); };

        var body=add(theme.horizontalList()).expandX().widget();
        double available=getWindowWidth()/Math.max(.75,theme.scale(1));
        results=body.add(theme.view()).minWidth(Math.min(255,available*.32)).top().widget();
        details=body.add(theme.view()).minWidth(Math.min(430,available*.50)).expandX().top().widget();
        results.maxHeight=details.maxHeight=Math.max(120,Math.min(540,getWindowHeight()-260));
        refilter(); select(selected);
    }
    private static void button(WHorizontalList row,String text,Runnable action) { row.add(row.theme.button(text)).expandX().widget().action=action; }
    private void refilter() {
        filtered.clear(); String query=search.get().strip().toLowerCase(Locale.ROOT);
        for(Entry entry:entries) if ((query.isEmpty() || entry.search.contains(query))
            && (category.get().equals("All categories") || entry.module.category.name.equals(category.get()))
            && (!activeOnly.checked || entry.module.isActive())) filtered.add(entry.module);
        page=Math.min(page,Math.max(0,(filtered.size()-1)/PAGE_SIZE)); rebuildResults();
    }
    private void rebuildResults() {
        results.clear(); visibleToggles.clear();
        results.add(theme.label(filtered.size()+" modules  /  page "+(page+1)));
        int start=page*PAGE_SIZE,end=Math.min(filtered.size(),start+PAGE_SIZE);
        for(int i=start;i<end;i++) {
            Module module=filtered.get(i);
            var row=results.add(theme.horizontalList()).expandX().widget();
            WCheckbox toggle=row.add(theme.checkbox(module.isActive())).widget();
            toggle.action=() -> { if(toggle.checked!=module.isActive()) module.toggle(); };
            WButton name=row.add(theme.button(module.title)).expandX().widget();
            name.tooltip=module.description; name.action=() -> select(module); visibleToggles.put(module,toggle);
        }
        if(filtered.isEmpty()) results.add(theme.label("No matching modules"));
        if(filtered.size()>PAGE_SIZE) {
            var pages=results.add(theme.horizontalList()).expandX().widget();
            button(pages,"Previous",() -> { if(page>0) { page--; rebuildResults(); } });
            button(pages,"Next",() -> { if((page+1)*PAGE_SIZE<filtered.size()) { page++; rebuildResults(); } });
        }
    }
    private void select(Module module) {
        selected=module; details.clear(); settingsContainer=null; selectedActive=null; keybind=null;
        if(module==null) { details.add(theme.label("Choose a module to configure it here.")); return; }
        details.add(theme.label(module.title,true));
        details.add(theme.label(module.description,380));
        if(module.addon!=null) details.add(theme.label(module.addon.name));
        var controls=details.add(theme.horizontalList()).expandX().widget();
        selectedActive=controls.add(theme.checkbox(module.isActive())).widget(); controls.add(theme.label("Enabled"));
        selectedActive.action=() -> { if(selectedActive.checked!=module.isActive()) module.toggle(); };
        controls.add(theme.label("Keybind"));
        keybind=controls.add(theme.keybind(module.keybind)).expandX().widget();
        keybind.actionOnSet=() -> Modules.get().setModuleToBind(module);
        module.settings.onActivated();
        if(!module.settings.groups.isEmpty()) {
            settingsContainer=details.add(theme.verticalList()).expandX().widget();
            settingsContainer.add(theme.settings(module.settings)).expandX();
        }
        var custom=module.getWidget(theme);
        if(custom!=null) details.add(custom).expandX();
        details.add(theme.button("Open full module screen")).expandX().widget().action=() -> mc.gui.setScreen(theme.moduleScreen(module));
    }
    @Override public void tick() {
        super.tick();
        if(selected!=null && settingsContainer!=null) selected.settings.tick(settingsContainer,theme);
        if(selectedActive!=null) selectedActive.checked=selected.isActive();
    }
    @Override public boolean shouldCloseOnEsc() { return !Modules.get().isBinding(); }
    @EventHandler private void activeChanged(ActiveModulesChangedEvent event) {
        if(activeOnly!=null && activeOnly.checked) refilter();
        else for(var entry:visibleToggles.entrySet()) entry.getValue().checked=entry.getKey().isActive();
    }
}

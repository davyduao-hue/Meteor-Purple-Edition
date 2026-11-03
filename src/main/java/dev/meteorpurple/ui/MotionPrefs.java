package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.util.Properties;
import java.io.IOException;

/** Tiny bootstrap preference cache: motion accessibility works before Meteor initializes. */
public final class MotionPrefs {
    private static boolean ui=true,game=false,reduced=false,performance=false;
    private static final Path FILE=FabricLoader.getInstance().getConfigDir().resolve("meteor-purple-edition-motion.properties");
    static {
        try(var in=Files.newInputStream(FILE)) {
            Properties p=new Properties(); p.load(in);
            ui=Boolean.parseBoolean(p.getProperty("ui","true")); game=Boolean.parseBoolean(p.getProperty("game","false"));
            reduced=Boolean.parseBoolean(p.getProperty("reduced","false")); performance=Boolean.parseBoolean(p.getProperty("performance","false"));
        } catch(IOException | SecurityException ignored) { }
    }
    private MotionPrefs() {}
    public static boolean ui() { Appearance a=Appearance.get(); return a==null ? ui && !reduced && !performance:a.animate(); }
    public static void save(boolean nextUi,boolean nextGame,boolean nextReduced,boolean nextPerformance) {
        if(ui==nextUi && game==nextGame && reduced==nextReduced && performance==nextPerformance) return;
        ui=nextUi; game=nextGame; reduced=nextReduced; performance=nextPerformance;
        try {
            Files.createDirectories(FILE.getParent()); Path tmp=FILE.resolveSibling(FILE.getFileName()+".tmp");
            Files.writeString(tmp,"ui="+ui+"\ngame="+game+"\nreduced="+reduced+"\nperformance="+performance+"\n");
            try { Files.move(tmp,FILE,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING); }
            catch(AtomicMoveNotSupportedException e) { Files.move(tmp,FILE,StandardCopyOption.REPLACE_EXISTING); }
        } catch(IOException | SecurityException e) { org.slf4j.LoggerFactory.getLogger("Zenith").warn("Could not save bootstrap motion preferences",e); }
    }
}

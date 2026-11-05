package dev.meteorpurple;

import dev.meteorpurple.ui.LoadingMotion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoadingMotionTest {
    @Test void stallsAndRepeatedProgressUpdatesStayBoundedAndSettle() {
        LoadingMotion m=new LoadingMotion(); long start=1_000_000_000L;
        for(int i=0;i<500;i++) {
            m.update(Math.min(1,i/100f),true,start+i*16_666_667L+(i>30 ? 60_000_000_000L:0));
            assertTrue(Float.isFinite(m.progress.value()));
            assertEquals(LoadingMotion.MAX_SPARKS,m.life.length);
            for(int j=0;j<m.life.length;j++) { assertTrue(Float.isFinite(m.px[j])); assertTrue(m.life[j]>=0 && m.life[j]<=.6f); }
        }
        assertEquals(1,m.progress.value(),.001);
        for(float life:m.life) assertEquals(0,life,.001);
    }
    @Test void reducedMotionUsesExactProgressAndClearsAllEffects() {
        LoadingMotion m=new LoadingMotion();
        m.update(.3f,true,1_000_000_000L); m.update(.7f,false,1_016_666_667L);
        assertEquals(.7f,m.progress.value()); assertEquals(1,m.enter.value());
        assertEquals(0,m.impact.value()); for(float life:m.life) assertEquals(0,life);
    }
}

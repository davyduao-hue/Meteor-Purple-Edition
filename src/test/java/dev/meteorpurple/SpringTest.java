package dev.meteorpurple;
import dev.meteorpurple.ui.Spring;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class SpringTest {
    @Test void motionSettlesAfterLongFramesWithoutInvalidGeometry() {
        Spring s=new Spring(0);
        for(int i=0;i<120;i++) {
            float v=s.update(1,i==8 ? 12:1/60f,16);
            assertTrue(Float.isFinite(v)); assertTrue(v>-.1 && v<1.2);
        }
        assertEquals(1,s.value(),.001);
    }
    @Test void impulseReturnsToRest() {
        Spring s=new Spring(0); s.kick(3);
        for(int i=0;i<180;i++) s.update(0,1/60f,18);
        assertEquals(0,s.value(),.001);
    }
}

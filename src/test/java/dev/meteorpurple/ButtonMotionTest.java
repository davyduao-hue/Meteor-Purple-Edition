package dev.meteorpurple;

import dev.meteorpurple.ui.ButtonMotion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ButtonMotionTest {
    @Test void particlesAndClickRippleAreOneShotAndStopUnderReducedMotion() {
        ButtonMotion m=new ButtonMotion(); long time=1_000_000_000L;
        m.update(true,true,170,time); assertEquals(0,m.burst());
        m.press(); m.update(true,true,170,time+16_666_667L); assertTrue(m.ripple()>=0);
        for(int i=2;i<65;i++) m.update(true,true,170,time+i*16_666_667L);
        assertEquals(-1,m.ripple()); assertEquals(-1,m.burst());
        m.press(); m.update(true,false,170,time+2_000_000_000L);
        assertEquals(-1,m.ripple()); assertEquals(0,m.pressAmount());
    }
    @Test void hoverSettlesAndShineStopsInsteadOfLooping() {
        ButtonMotion motion=new ButtonMotion();
        long now=1_000_000_000L;
        for(int i=0;i<60;i++) motion.update(true,true,170,now+i*16_666_667L);
        assertEquals(1,motion.amount(),.001);
        assertEquals(-1,motion.sweep());
        for(int i=60;i<90;i++) motion.update(false,true,170,now+i*16_666_667L);
        assertEquals(0,motion.amount(),.001);
        motion.update(true,true,170,now+1_600_000_000L);
        assertEquals(0,motion.sweep());
    }
    @Test void reducedMotionChangesImmediatelyAndSuppressesShine() {
        ButtonMotion motion=new ButtonMotion();
        motion.update(true,false,170,1_000_000_000L);
        assertEquals(1,motion.amount());
        assertEquals(-1,motion.sweep());
        motion.update(false,false,170,1_010_000_000L);
        assertEquals(0,motion.amount());
    }
}

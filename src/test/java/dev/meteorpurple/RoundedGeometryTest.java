package dev.meteorpurple;

import dev.meteorpurple.ui.RoundedGeometry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoundedGeometryTest {
    @Test void mergedSpansPreserveEveryPixelOfTheOldRoundedCorners() {
        for(int radius=0;radius<=96;radius++) {
            int row=0; int[] spans=RoundedGeometry.spans(radius);
            for(int i=0;i<spans.length;i+=3) {
                assertEquals(row,spans[i]); assertTrue(spans[i+1]>row);
                for(;row<spans[i+1];row++) {
                    int expected=(int)Math.ceil(radius-Math.sqrt(radius*radius-Math.pow(radius-row-.5,2)));
                    assertEquals(expected,spans[i+2],"radius="+radius+", row="+row);
                }
            }
            assertEquals(radius,row);
            if(radius>=12) assertTrue(spans.length/3<radius*.75,"coalesces duplicate spans");
        }
    }
}

package dev.meteorpurple;

import dev.meteorpurple.modules.Appearance;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaletteContrastTest {
    @Test void bodyTextRemainsReadableAcrossAllPalettes() {
        for(var style:Appearance.Style.values()) {
            assertTrue(contrast(style.palette.text,style.palette.base)>=4.5,style.title+" body contrast");
            assertTrue(contrast(style.palette.text,style.palette.surface1)>=4.5,style.title+" selected-surface contrast");
            assertTrue(contrast(style.palette.text,style.palette.surface2)>=4.5,style.title+" control contrast");
            assertTrue(contrast(style.palette.muted,style.palette.base)>=4.5,style.title+" secondary text contrast");
        }
    }
    private static double contrast(Color a,Color b) { double x=luminance(a),y=luminance(b); return (Math.max(x,y)+.05)/(Math.min(x,y)+.05); }
    private static double luminance(Color c) { return .2126*linear(c.r)+.7152*linear(c.g)+.0722*linear(c.b); }
    private static double linear(int channel) { double value=channel/255.; return value<=.04045 ? value/12.92:Math.pow((value+.055)/1.055,2.4); }
}

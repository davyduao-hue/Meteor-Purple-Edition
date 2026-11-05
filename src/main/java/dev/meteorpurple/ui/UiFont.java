package dev.meteorpurple.ui;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.*;

/** Bundled theme-scoped typeface, without changing Meteor's global font preference. */
public final class UiFont {
    public static final String NAME="zenith-fredoka-medium",FAMILY="Zenith Fredoka";
    public static final FontFace FACE=new BuiltinFontFace(new FontInfo(FAMILY,FontInfo.Type.Regular),NAME);
    private UiFont() {}
    public static void registerFamily() {
        if(Fonts.getFamily(FAMILY)!=null) return;
        FontFamily family=new FontFamily(FAMILY); family.addFont(FACE); Fonts.FONT_FAMILIES.add(family);
    }
}

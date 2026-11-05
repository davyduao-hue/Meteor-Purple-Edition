package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance.Style;

/** A design system per theme: shape, density and line treatment, beyond a color swap. */
public record ThemeDesign(int windowRadius,int controlRadius,int headerTint,int rowTint,boolean outlineIcons) {
    private static final ThemeDesign[] DESIGNS={
        new ThemeDesign(14,8,22,48,false),new ThemeDesign(18,11,32,44,false),
        new ThemeDesign(6,4,8,28,true),new ThemeDesign(20,11,16,35,false),
        new ThemeDesign(10,6,28,40,true),new ThemeDesign(17,10,26,45,false),
        new ThemeDesign(13,7,24,42,true),new ThemeDesign(16,8,20,40,true),
        new ThemeDesign(5,3,8,30,true),new ThemeDesign(5,3,24,48,false)
    };
    public static ThemeDesign of(Style style) { return DESIGNS[style.ordinal()]; }
    public static ThemeDesign current() { var a=dev.meteorpurple.modules.Appearance.get(); return of(a==null ? Style.Mocha:a.style.get()); }
}

package dev.meteorpurple.ui;

import me.pindour.catppuccin.api.render.shape.RoundedRect;
import meteordevelopment.meteorclient.utils.render.color.Color;

/** Shared native Catppuccin batching, with cached colors instead of new palettes per row. */
public final class ClassicPaint {
    private static Palette previous;
    private static final Color[] COLORS=new Color[8];
    public static final Color WHITE=new Color(255,255,255,255);
    public static final Color CLEAR=new Color(0,0,0,0);
    static { for(int i=0;i<COLORS.length;i++) COLORS[i]=new Color(); }
    private ClassicPaint() {}
    public static boolean enabled() { var a=dev.meteorpurple.modules.Appearance.get(); return a!=null && a.customClientUi.get(); }
    public static Color edge() { cache(); return COLORS[0]; }
    public static Color header() { cache(); return COLORS[1]; }
    public static Color faint() { cache(); return COLORS[2]; }
    public static Color row() { cache(); return COLORS[3]; }
    public static Color shadow() { cache(); return COLORS[4]; }
    private static void cache() {
        Palette p=Canvas.palette(); if(previous==p) return; previous=p;
        COLORS[0].set(p.accent.r,p.accent.g,p.accent.b,p.light ? 55:50);
        int tint=ThemeDesign.current().headerTint();
        COLORS[1].set(p.accent.r,p.accent.g,p.accent.b,tint);
        COLORS[2].set(p.accent.r,p.accent.g,p.accent.b,25);
        COLORS[3].set(p.accent.r,p.accent.g,p.accent.b,ThemeDesign.current().rowTint());
        COLORS[4].set(0,0,0,p.light ? 15:38);
    }
    public static void rect(double x,double y,double w,double h,double radius,Color fill) {
        if(w>0 && h>0 && fill.a>0) RoundedRect.get().pos(x,y).size(w,h).radius(radius).color(fill).render();
    }
    public static void panel(double x,double y,double w,double h,double radius,Color fill,Color edge) {
        if(w>0 && h>0) RoundedRect.get().pos(x,y).size(w,h).radius(radius).color(fill).outline(edge,1).render();
    }
    public static Color mix(Color out,Color a,Color b,double t) {
        t=Math.max(0,Math.min(1,t)); out.set((int)(a.r+(b.r-a.r)*t),(int)(a.g+(b.g-a.g)*t),(int)(a.b+(b.b-a.b)*t),(int)(a.a+(b.a-a.a)*t)); return out;
    }
}

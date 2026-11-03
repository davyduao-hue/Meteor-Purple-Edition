package dev.meteorpurple.ui;
import meteordevelopment.meteorclient.utils.render.color.Color;

/** Precomputed colors shared by widgets, native screens and HUDs. */
public final class Palette {
    public final Color base, mantle, crust, surface0, surface1, surface2, accent, text, muted;
    public final boolean light;
    public Palette(int base, int mantle, int accent, int text, boolean light) {
        this(color(base),color(mantle),color(accent),color(text),light);
    }
    public Palette(Color base,Color mantle,Color accent,Color text,boolean light) {
        this.base=new Color(base); this.mantle=new Color(mantle); this.accent=new Color(accent); this.text=new Color(text); this.light=light;
        crust=mix(this.mantle,light ? color(0xffffff):color(0x000000),.24);
        surface0=mix(this.base,this.accent,.06); surface1=mix(this.base,this.accent,.11); surface2=mix(this.base,this.accent,.18);
        muted=mix(this.text,this.base,.26);
    }
    public static Color color(int rgb) { return new Color(rgb>>16&255,rgb>>8&255,rgb&255); }
    private static Color mix(Color a,Color b,double t) { return new Color((int)(a.r+(b.r-a.r)*t),(int)(a.g+(b.g-a.g)*t),(int)(a.b+(b.b-a.b)*t)); }
    public static int argb(Color c,int alpha) { return alpha<<24 | c.r<<16 | c.g<<8 | c.b; }
}

package dev.meteorpurple.ui;

import dev.meteorpurple.modules.Appearance;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.renderer.packer.TextureRegion;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import java.util.Locale;

/** Ten compact atlases, packed once with Meteor's own GUI. Each glyph is a cached UV view. */
public final class ThemeIcons {
    public enum Icon { ARROW,BOOKMARK_NO,BOOKMARK_YES,BRUSH,COPY,CUBE,EDIT,EYE,GRID,IMPORT,LINK,MINUS,MOUSE,MOVEMENT,PEOPLE,PERSON,PLUS,QUESTION_MARK,RESET,SEARCH,SETTING,SWORD,TICK,LOGO,GLOBE,ACCESSIBILITY }
    private static final Identifier[] IDS=new Identifier[Appearance.Style.values().length];
    private static final GuiTexture[][] TEXTURES=new GuiTexture[IDS.length][Icon.values().length];
    private static boolean initialized;
    static { for(var style:Appearance.Style.values()) IDS[style.ordinal()]=Identifier.fromNamespaceAndPath("meteor-purple-edition","textures/icons/"+style.name().toLowerCase(Locale.ROOT)+".png"); }
    private ThemeIcons() {}
    public static void init() {
        if(initialized) return;
        for(int s=0;s<IDS.length;s++) {
            GuiTexture atlas=GuiRenderer.addTexture(IDS[s]);
            for(var icon:Icon.values()) TEXTURES[s][icon.ordinal()]=new Glyph(atlas,icon.ordinal());
        }
        initialized=true;
    }
    public static boolean ready() { return initialized; }
    public static boolean owns(GuiTexture texture) { return texture instanceof Glyph; }
    public static boolean active() { var a=Appearance.get(); return initialized && a!=null && a.customClientUi.get(); }
    public static GuiTexture get(Icon icon) { return get(Appearance.get()==null ? Appearance.Style.Mocha:Appearance.get().style.get(),icon); }
    public static GuiTexture get(Appearance.Style style,Icon icon) { return TEXTURES[style.ordinal()][icon.ordinal()]; }
    public static void draw(GuiGraphicsExtractor g,Appearance.Style style,Icon icon,int x,int y,int size,int alpha) {
        int i=icon.ordinal();
        g.blit(RenderPipelines.GUI_TEXTURED,IDS[style.ordinal()],x,y,i%6*48,i/6*48,size,size,48,48,288,240,(alpha<<24)|0xffffff);
    }
    private static final class Glyph extends GuiTexture {
        private final GuiTexture atlas;
        private final int index;
        private final TextureRegion region=new TextureRegion(48,48);
        private Glyph(GuiTexture atlas,int index) { this.atlas=atlas; this.index=index; }
        @Override public TextureRegion get(double width,double height) {
            TextureRegion all=atlas.get(10000,10000); double du=(all.x2-all.x1)/6,dv=(all.y2-all.y1)/5;
            region.x1=all.x1+index%6*du; region.x2=region.x1+du;
            region.y1=all.y1+index/6*dv; region.y2=region.y1+dv;
            return region;
        }
    }
}

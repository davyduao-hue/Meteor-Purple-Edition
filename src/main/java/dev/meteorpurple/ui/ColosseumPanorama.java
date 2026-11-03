package dev.meteorpurple.ui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.CubeMapTexture;
import net.minecraft.resources.Identifier;
public final class ColosseumPanorama {
    public static final Identifier TEXTURE=Identifier.fromNamespaceAndPath("meteor-purple-edition","textures/panorama/colosseum");
    private static boolean loaded;
    private ColosseumPanorama() {}
    public static void select(boolean enabled) {
        var manager=Minecraft.getInstance().getTextureManager();
        if(enabled && !loaded) { manager.registerAndLoad(TEXTURE,new CubeMapTexture(TEXTURE)); loaded=true; }
        else if(!enabled && loaded) { manager.release(TEXTURE); loaded=false; }
    }
}

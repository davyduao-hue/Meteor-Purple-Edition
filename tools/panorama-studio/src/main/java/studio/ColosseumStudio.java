package studio;

import net.fabricmc.api.ClientModInitializer;
import studio.mixin.HudAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.gamerules.GameRules;
import java.nio.file.*;
import java.util.*;

/** Builds the actual Minecraft scene and captures six native 90-degree camera views with Iris. */
public final class ColosseumStudio implements ClientModInitializer {
    private static final String WORLD="Zenith-Colosseum";
    private record Voxel(int x,int y,int z,BlockState state) {}
    private final List<Voxel> blocks=new ArrayList<>();
    private boolean opened,prepared,built;
    private volatile boolean ready;
    private int placed,wait,face=-1;
    private final Path output=Path.of("../captures");
    public void onInitializeClient() {
        try { Files.createDirectories(output); } catch(Exception e) { throw new RuntimeException(e); }
        ServerTickEvents.END_SERVER_TICK.register(this::serverTick);
        ClientTickEvents.END_CLIENT_TICK.register(this::clientTick);
    }
    private void command(MinecraftServer s,String text) { s.getCommands().performPrefixedCommand(s.createCommandSourceStack(),text); }
    private void serverTick(MinecraftServer s) {
        if(s.getPlayerList().getPlayers().isEmpty()) return;
        ServerLevel level=s.overworld();
        if(!prepared) {
            prepared=true;
            var rules=level.getGameRules();
            rules.set(GameRules.ADVANCE_TIME,false,s); rules.set(GameRules.ADVANCE_WEATHER,false,s);
            rules.set(GameRules.SPAWN_MOBS,false,s); rules.set(GameRules.RANDOM_TICK_SPEED,0,s);
            command(s,"setworldspawn 0 76 12"); command(s,"time set 22500"); command(s,"weather clear");
            var player=s.getPlayerList().getPlayers().getFirst();
            player.setGameMode(GameType.SPECTATOR);
            player.teleportTo(level,320,90,320,Set.of(),0,0,true);
            if(Files.exists(Path.of("saves",WORLD,"zenith-built.txt"))) { built=true; placed=-1; }
            else build();
            System.out.println("ZENITH_STUDIO build_voxels="+blocks.size());
        }
        if(!built) {
            int end=Math.min(placed+4000,blocks.size());
            BlockPos.MutableBlockPos pos=new BlockPos.MutableBlockPos();
            for(;placed<end;placed++) { Voxel b=blocks.get(placed); level.setBlock(pos.set(b.x,b.y,b.z),b.state,18); }
            if(placed==blocks.size()) {
                blocks.clear(); built=true;
                try { Files.writeString(Path.of("saves",WORLD,"zenith-built.txt"),"Zenith Colosseum: native block build, seed 6582, dawn 22500.\n"); } catch(Exception e) { throw new RuntimeException(e); }
                System.out.println("ZENITH_STUDIO build_complete");
            }
        }
        if(built && !ready) {
            // A ruined western arcade opens the moon's low dawn sightline.
            for(int x=-82;x<-52;x++) for(int z=-29;z<=29;z++) {
                double r=Math.sqrt(x*x/6400.0+z*z/4356.0);
                if(r<.935 || r>1.0) continue;
                int top=82+Math.abs(z)/5+Math.floorMod(x+z,3);
                for(int y=top;y<=107;y++) level.setBlock(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState(),18);
            }
            s.getPlayerList().getPlayers().getFirst().teleportTo(level,0,76,12,Set.of(),0,0,true);
            ready=true;
        }
    }
    private void put(int x,int y,int z,Block block) { blocks.add(new Voxel(x,y,z,block.defaultBlockState())); }
    private Block stone(int x,int y,int z) {
        int noise=Math.floorMod(x*7349+y*379+z*193,29);
        return noise==0 ? Blocks.MOSSY_STONE_BRICKS:noise<4 ? Blocks.CRACKED_STONE_BRICKS:noise<8 ? Blocks.CUT_SANDSTONE:Blocks.SMOOTH_SANDSTONE;
    }
    private void build() {
        // A 160x132 oval, three arcades, 48 bays, stepped seating and four processional entrances.
        for(int x=-86;x<=86;x++) for(int z=-74;z<=74;z++) {
            double r=Math.sqrt(x*x/6400.0+z*z/4356.0);
            if(r>1.07) continue;
            for(int y=60;y<=63;y++) put(x,y,z,Blocks.STONE_BRICKS);
            double arena=Math.sqrt(x*x/1936.0+z*z/1024.0);
            boolean avenue=Math.abs(x)<4 || Math.abs(z)<4;
            put(x,64,z,arena<1 ? (Math.floorMod(x*31+z*7,17)==0 ? Blocks.SMOOTH_SANDSTONE:Blocks.SANDSTONE):Blocks.CUT_SANDSTONE);
            if(arena>=1 && r<.91 && !avenue) {
                int h=Math.max(0,(int)((r-.52)*45));
                for(int y=65;y<=65+h;y++) put(x,y,z,stone(x,y,z));
                if(h%2==0) put(x,66+h,z,Blocks.SANDSTONE_SLAB);
            }
            if(r>=.935 && r<=1.0) {
                double theta=Math.atan2(z/66.0,x/80.0);
                double bay=theta/(2*Math.PI)*48;
                double local=Math.abs(bay-Math.floor(bay+.5))*9.5;
                boolean entrance=avenue;
                for(int y=65;y<=106;y++) {
                    int tier=(y-65)/14,dy=(y-65)%14;
                    boolean arch=local<2.7 && dy>=1 && (dy<=6 || local*local+(dy-6)*(dy-6)<7.5);
                    if(entrance && y<75) continue;
                    if(arch && dy<10) continue;
                    // A few broken upper bays make the silhouette feel like a real ancient arena.
                    if(tier==2 && x>36 && z<-22 && y>97+(Math.abs(x+z)%4)) continue;
                    Block material=dy==0 || dy>=12 ? Blocks.CHISELED_SANDSTONE:stone(x,y,z);
                    if(local>4 && r>.974) material=Blocks.QUARTZ_PILLAR;
                    put(x,y,z,material);
                }
            }
            if(r>=1.0 && r<1.035 && (Math.abs(x)%12<2 || Math.abs(z)%12<2)) {
                for(int y=65;y<70;y++) put(x,y,z,Blocks.CHISELED_SANDSTONE);
            }
            if(r>.91 && r<.94 && Math.floorMod(x*13+z*5,83)==0) {
                put(x,65,z,Blocks.POLISHED_BLACKSTONE_BRICKS); put(x,66,z,Blocks.SOUL_LANTERN);
            }
        }
        // Violet Meteor monument with raised streaks, a solid cube and a lit stone plinth.
        for(int x=-2;x<=12;x++) for(int z=-20;z<=-6;z++) {
            for(int y=65;y<=66;y++) put(x,y,z,Blocks.POLISHED_BLACKSTONE_BRICKS);
            if((x==-2 || x==12) && (z==-20 || z==-6)) put(x,67,z,Blocks.SEA_LANTERN);
        }
        for(int x=2;x<=9;x++) for(int y=67;y<=74;y++) for(int z=-16;z<=-9;z++)
            put(x,y,z,(x==2 || y==74) ? Blocks.AMETHYST_BLOCK:Blocks.PURPUR_BLOCK);
        for(int line=0;line<3;line++) for(int t=0;t<17;t++) {
            int px=1-t,pz=-12-line*4,py=70+line*3+t/2;
            for(int a=0;a<2;a++) for(int b=0;b<2;b++) put(px,py+a,pz+b,Blocks.AMETHYST_BLOCK);
        }
        // Inlaid radial floor lights, kept sparse to preserve moonlit shadows.
        for(int i=0;i<16;i++) {
            double a=i*Math.PI/8;
            int x=(int)(Math.cos(a)*40),z=(int)(Math.sin(a)*29);
            put(x,64,z,Blocks.SEA_LANTERN);
        }
    }
    private void clientTick(Minecraft mc) {
        if(mc.gui.overlay()!=null) return;
        if(!opened) {
            if(!(mc.gui.screen() instanceof TitleScreen)) return;
            opened=true; mc.options.pauseOnLostFocus=false;
            if(Files.exists(Path.of("saves",WORLD,"level.dat"))) mc.createWorldOpenFlows().openWorld(WORLD,() -> {});
            else mc.createWorldOpenFlows().createFreshLevel(WORLD,
                new LevelSettings("Zenith Moonrise Colosseum",GameType.CREATIVE,new LevelSettings.DifficultySettings(Difficulty.PEACEFUL,false,false),true,WorldDataConfiguration.DEFAULT),
                new WorldOptions(6582,false,false),
                lookup -> lookup.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT).value().createWorldDimensions(),mc.gui.screen());
            return;
        }
        if(!ready || mc.player==null || mc.level==null) return;
        mc.gui.setScreen(null); ((HudAccess)mc.gui.hud).zenith$hidden(true);
        mc.gameRenderer.setRenderBlockOutline(false);
        if(face<0 && ++wait<240) return;
        if(face<0) { face=0; wait=0; mc.gameRenderer.mainCamera().enablePanoramicMode(); orient(mc); return; }
        if(++wait<100) return;
        wait=0;
        final int index=face;
        Screenshot.takeScreenshot(mc.gameRenderer.mainRenderTarget(),image -> {
            try { image.writeToFile(output.resolve("panorama_"+index+".png")); }
            catch(Exception e) { throw new RuntimeException(e); } finally { image.close(); }
        });
        System.out.println("ZENITH_STUDIO capture="+face+" camera="+mc.gameRenderer.mainCamera().getFov());
        if(++face<6) orient(mc);
        else {
            try {
                Class<?> iris=Class.forName("net.irisshaders.iris.api.v0.IrisApi");
                Object api=iris.getMethod("getInstance").invoke(null);
                boolean enabled=(boolean)iris.getMethod("isShaderPackInUse").invoke(api);
                if(!enabled) throw new AssertionError("Iris shader pack was not active");
                Files.writeString(output.resolve("CAPTURE.txt"),"Native Minecraft 26.2 block colosseum; Iris 1.11.4; Sodium 0.9.2; Complementary Reimagined r5.9.3; shader active; seed 6582; frozen dawn 22500; six 90-degree views.\n");
            } catch(Exception e) { throw new RuntimeException(e); }
            mc.stop();
        }
    }
    private void orient(Minecraft mc) {
        float yaw=switch(face) { case 1 -> 90; case 2 -> 180; case 3 -> -90; default -> 0; };
        float pitch=face==4 ? -90:face==5 ? 90:0;
        mc.player.setYRot(yaw); mc.player.yRotO=yaw; mc.player.setXRot(pitch); mc.player.xRotO=pitch;
    }
}

package sponge.Utils;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.IsoworldsSponge;

import java.util.Collection;

/**
 * Created by Edwin on 30/11/2017.
 */
public class IsoWorldsBanWorldItems {

    private static final IsoworldsSponge plugin = IsoworldsSponge.instance;

    public static void checkLoadedChunks() {
        Collection<World> loadedWorlds = Sponge.getServer().getWorlds();
        Sponge.getScheduler().createAsyncExecutor(plugin).execute(new Runnable() {
            public void run() {
                loadedWorlds.forEach(world -> {
                    Iterable<Chunk> loadedChunks = world.getLoadedChunks();
                    loadedChunks.forEach(chunk -> {
                        Vector3i min = chunk.getBlockMin();
                        Vector3i max = chunk.getBlockMax();
                        for (int x = min.getX(); x <= max.getX(); x++) {
                            for (int y = min.getY(); y <= max.getY(); y++) {
                                for (int z = min.getZ(); z <= max.getZ(); z++) {
                                    BlockState block = chunk.getBlock(x, y, z);
                                    Location blockLoc = chunk.getLocation(x, y, z);
                                    if (block.getType().getName().contains("magicbees:hiveblock")) {
                                        int finalX = x;
                                        int finalY = y;
                                        int finalZ = z;
                                        Sponge.getScheduler().createTaskBuilder().execute(() -> {
                                            blockLoc.setBlock(BlockTypes.AIR.getDefaultState(), BlockChangeFlag.ALL, Cause.of(NamedCause.owner(plugin)));
                                            IsoworldsUtils.cm("[IsoWorlds-BanItemWorlds]: " + block.getType().getName() + " at x:" + finalX + " y:" + finalY + " z:" + finalZ);
                                        }).submit(plugin);
                                    }
                                }
                            }
                        }
                    });
                });
            }
        });
    }
}

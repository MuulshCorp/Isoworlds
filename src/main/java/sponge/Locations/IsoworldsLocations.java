package sponge.Locations;

import org.spongepowered.api.event.cause.Cause;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.AbstractProperty;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by Edwin on 08/10/2017.
 */

public class IsoworldsLocations {

    private static final IsoworldsSponge plugin = IsoworldsSponge.instance;

    public static Optional<Location<World>> getHighestLoc(Location<World> loc) {
        Optional<Integer> y = getHighestY(loc.getExtent(), loc.getX(), loc.getZ());
        return y.map(integer -> new Location<>(loc.getExtent(), loc.getX(), integer, loc.getZ()));
    }

    private static Optional<Integer> getHighestY(World w, Integer x, Integer z) {
        return getHighestY(w, (double) x, (double) z);
    }

    private static boolean isPassable(World w, Double x, int y, Double z) {
        Optional<PassableProperty> prop = new Location<>(w, x, y, z).getBlock().getProperty(PassableProperty.class);
        return prop.map(AbstractProperty::getValue).orElse(false);
    }

    private static Optional<Integer> getHighestY(World w, Double x, Double z) {
        int y = w.getBlockMax().getY();
        while (isPassable(w, x, y, z)) {
            if (!w.getBlock(x.intValue(), y, z.intValue()).getType().equals(BlockTypes.AIR)) {
                return Optional.of(y);
            }
            y = y - 1;
            if (y <= 0) {
                return Optional.empty();
            }
        }
        return Optional.of(y);
    }

    public static boolean teleport(Player player, String world) {

        // Define dimension name
        if (world.equals("end")) {
            world = "DIM1";
        } else if (world.equals("nether")) {
            world = "DIM-1";
        }

        Location<World> maxy;

        Optional<World> finalWorld = plugin.getGame().getServer().getWorld(world);
        if (finalWorld.isPresent()) {
            Location<World> spawn = finalWorld.get().getSpawnLocation();

            maxy = new Location<>(spawn.getExtent(), 0, 0, 0);

            Location<World> top = IsoworldsLocations.getHighestLoc(maxy).orElse(null);
            Location<World> secours;
            Location<World> go = new Location<>(spawn.getExtent(), 0, 60, 0);
            Location<World> safeBlock;

            try {
                if (top == null) {
                    finalWorld.get().getLocation(go.getBlockPosition()).setBlockType(BlockTypes.DIRT, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
                    go = new Location<>(spawn.getExtent(), 0, 61, 0);
                } else {
                    secours = IsoworldsLocations.getHighestLoc(maxy).orElse(null);
                    go = new Location<>(spawn.getExtent(), 0, secours.getBlockY() + 1, 0);
                    safeBlock = new Location<>(spawn.getExtent(), 0, secours.getBlockY(), 0);
                    finalWorld.get().getLocation(safeBlock.getBlockPosition()).setBlockType(BlockTypes.DIRT, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }


            // Téléportation du joueur
            if (player.setLocationSafely(go)) {
                IsoworldsUtils.cm("Le joueur a bien été téléporté !");
            } else {
                IsoworldsUtils.cm("Le joueur n'a pas pu être téléporté !");
                return false;
            }
        }
        return true;
    }
}

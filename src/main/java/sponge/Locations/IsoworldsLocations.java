package sponge.Locations;

import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.event.cause.Cause;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsLogger;
import sponge.Utils.IsoworldsUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.AbstractProperty;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntToDoubleFunction;

/**
 * Created by Edwin on 08/10/2017.
 */

public class IsoworldsLocations {

    private static final IsoworldsSponge plugin = IsoworldsSponge.instance;

    public static Optional<Location<World>> getHighestLoc(Location<World> loc) {
        Optional<Integer> y = getHighestY(loc.getExtent(), loc.getX(), loc.getZ());
        return y.map(integer -> new Location<>(loc.getExtent(), loc.getX(), integer + 1, loc.getZ()));
    }

    private static boolean isPassable(World w, Double x, int y, Double z) {
        Optional<MatterProperty> prop = new Location<>(w, x, y, z).getBlock().getProperty(MatterProperty.class);
        return prop.get().getValue().toString().equals("GAS");
    }

    private static Optional<Integer> getHighestY(World w, Double x, Double z) {
        int y = w.getBlockMax().getY();
        while (isPassable(w, x, y, z)) {
            y = y - 1;
            if (y <= 0) {
                return Optional.empty();
            }
        }
        return Optional.of(y);
    }

    public static boolean teleport(Player player, String worldname) {

        Location<World> maxy;
        Optional<World> finalWorld = plugin.getGame().getServer().getWorld(getOfficialDimSpawn(worldname));

        if (finalWorld.isPresent()) {

            // Actual spawn location
            Location<World> spawn = finalWorld.get().getSpawnLocation();

            // Max location, y is set blockmax later so don't care
            maxy = IsoworldsLocations.getHighestLoc(new Location<>(spawn.getExtent(), getAxis("x"), 0, getAxis("z")))
                    .orElse(new Location<>(spawn.getExtent(), getAxis("x"), 61, getAxis("z")));

            Location<World> destination = new Location<>(spawn.getExtent(), getAxis("x"), maxy.getBlockY(), getAxis("z"));

            try {
                // If dimensions if not autobuilt, return the same name so it can build isoworlds safe zone
                if (getOfficialDimSpawn(worldname).equals(worldname)) {

                    // Set dirt if liquid or air
                    if (!finalWorld.get().getBlock(getAxis("x").intValue(), destination.getBlockY() - 1, getAxis("z").intValue()).getProperty(MatterProperty.class).get().getValue().toString().equals("SOLID")) {
                        // Build safe zone
                        finalWorld.get().getLocation(destination.getBlockX(), destination.getBlockY() - 1, destination.getBlockZ()).setBlockType(BlockTypes.DIRT, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
                    }
                }

                // Téléportation du joueur
                if (player.setLocationSafely(destination)) {
                    IsoworldsUtils.cm("Le joueur a bien été téléporté !");
                } else {
                    IsoworldsUtils.cm("Le joueur n'a pas pu être téléporté !");
                    return false;
                }

            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

        }
        return true;
    }

    private static void buildSafeSpawn(String worldname, String casualName) {
        Optional<World> finalWorld = plugin.getGame().getServer().getWorld(worldname);
        Location<World> spawn = finalWorld.get().getSpawnLocation();
        Location<World> go = new Location<>(spawn.getExtent(), 0, 61, 0);

        // Clear zone
        for (int x = -2; x < 2; x++) {
            for (int y = 60; y < 65; y++) {
                for (int z = -2; z < 2; z++) {
                    if (Sponge.getServer().getWorld(worldname).get().getBlock(x, y, x).getType() != BlockTypes.BEDROCK) {
                        Sponge.getServer().getWorld(worldname).get().setBlockType(x, y, z, BlockTypes.AIR, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
                    }
                }
            }
        }

        // Build safe zone
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Sponge.getServer().getWorld(worldname).get().setBlockType(x, 60, z, BlockTypes.BEDROCK, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
            }
        }

        // Set sign
        Sponge.getServer().getWorld(worldname).get().setBlockType(-1, 61, -2, BlockTypes.TORCH, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
        Sponge.getServer().getWorld(worldname).get().setBlockType(-2, 61, -2, BlockTypes.WALL_SIGN, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());


//        Block block = Bukkit.getServer().getWorld(worldname).getBlockAt(-2, 61, -2);
//        BlockState state = block.getState();
//        Sign sign = (Sign) state;
//
//        sign.setLine(0, "Bienvenue");
//        sign.setLine(1, "Prenez garde...");
//        sign.setLine(2, "[" + casualName + "]");
//        sign.update();
    }

    private static Double getAxis(String axis) {

        // Define coordinate
        Map<String, Double> spawn = new HashMap<String, Double>();

        // INT THE SEA
        if (plugin.servername.equals("ITS")) {
            spawn.put("x", -1110.0);
            spawn.put("y", 102.0);
            spawn.put("z", 545.0);
        } else {
            spawn.put("x", 0.500);
            spawn.put("y", 60.0);
            spawn.put("z", 0.500);
        }

        return spawn.get(axis);
    }

    // Get name, null if not official
    private static String getOfficialDimSpawn(String worldname) {

        // Define dimension name
        if (worldname.equals("end")) {
            worldname = "DIM1";
            buildSafeSpawn(worldname, "END");
            return worldname;
        } else if (worldname.equals("nether")) {
            // Teleport to nether
            worldname = "DIM-1";
            buildSafeSpawn(worldname, "NETHER");
            return worldname;
        }
        return worldname;
    }
}

package bukkit.Locations;

import bukkit.IsoworldsBukkit;
import common.Cooldown;
import common.Msg;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edwin on 21/10/2017.
 */
public class IsoworldsLocations {

    private static final IsoworldsBukkit plugin = IsoworldsBukkit.instance;

    public static void teleport(Player player, String worldname) {

        // Construction du point de respawn
        int maxy;
        Location finalWorld = plugin.getServer().getWorld(getOfficialDimSpawn(worldname)).getSpawnLocation();

        try {

            // Actual spawn location
            Location spawn = finalWorld.getWorld().getSpawnLocation();

            // Set to 61 for official dimensions
            Location destination = new Location(spawn.getWorld(), getAxis("x"), 61, getAxis("z"));

            // If dimensions if not autobuilt, return the same name so it can build isoworlds safe zone
            if (getOfficialDimSpawn(worldname).equals(worldname)) {

                // Max location, y is set blockmax later so don't care
                maxy = spawn.getWorld().getHighestBlockYAt(getAxis("x").intValue(), getAxis("z").intValue());

                if (maxy == 0) {
                    maxy = 61;
                }

                destination = new Location(spawn.getWorld(), getAxis("x"), maxy, getAxis("z"));

                // Set dirt if liquid or air
                if (!finalWorld.getWorld().getBlockAt(getAxis("x").intValue(), destination.getBlockY() - 1, getAxis("z").intValue()).getType().isSolid()) {
                    // Build safe zone
                    finalWorld.getWorld().getBlockAt (destination.getBlockX(), destination.getBlockY() - 1, destination.getBlockZ()).setType(Material.DIRT);
                }
            }

            // Téléportation du joueur
            if (player.teleport(destination)) {
                player.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION);
                plugin.cooldown.addPlayerCooldown(player, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
            }

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }

    private static void buildSafeSpawn(String worldname, String casualName) {

        Bukkit.getServer().createWorld(new WorldCreator(worldname));

        // Clear zone
        for (int x = -2; x < 2; x++) {
            for (int y = 60; y < 65; y++) {
                for (int z = -2; z < 2; z++) {
                    if (Bukkit.getServer().getWorld(worldname).getBlockAt(x, y, z).getType() != Material.BEDROCK) {
                        Bukkit.getServer().getWorld(worldname).getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
        }

        // Build safe zone
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Bukkit.getServer().getWorld(worldname).getBlockAt(x, 60, z).setType(Material.BEDROCK);
            }
        }

        // Set sign
        //Bukkit.getServer().getWorld(worldname).getBlockAt(-2, 61, -2).setType(Material.TORCH);
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
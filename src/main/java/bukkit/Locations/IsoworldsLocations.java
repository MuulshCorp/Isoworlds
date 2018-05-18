package bukkit.Locations;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsLogger;
import common.Cooldown;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Created by Edwin on 21/10/2017.
 */
public class IsoworldsLocations {

    private static final IsoworldsBukkit plugin = IsoworldsBukkit.instance;

    public static void teleport(Player player, String worldname) {

        // If spawn already built if = 1
        int safe = 0;

        // Define dimension name
        if (worldname.equals("end")) {
            worldname = "DIM1";
            buildSafeSpawn(player, worldname, "END");
            safe = 1;
        } else if (worldname.equals("nether")) {
            // Teleport to nether
            worldname = "DIM-1";
            buildSafeSpawn(player, worldname, "NETHER");
            safe = 1;
        }

        // Construction du point de respawn
        Integer secours;
        Location go = new Location(Bukkit.getServer().getWorld(worldname), 0, 60, 0);
        Integer top = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);

        IsoworldsLogger.severe("Y = " + top);

        try {
            if (top <= 0) {
                IsoworldsLogger.severe("1");
                // Set dirt if liquid or air
                if (safe == 0 && !Bukkit.getServer().getWorld(worldname).getBlockAt(go).getType().isSolid()) {
                    // Build safe zone
                    for (int x = -1; x < 1; x++) {
                        for (int z = -1; z < 1; z++) {
                            Bukkit.getServer().getWorld(worldname).getBlockAt(x, 60, z).setType(Material.DIRT);
                        }
                    }
                }
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, 61, 0);
            } else {
                IsoworldsLogger.severe("2");
                secours = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, secours, 0);

                // Set block if air or liquid
                if (safe == 0 & !go.getBlock().getType().isSolid()) {
                    // Build safe zone
                    for (int x = -1; x < 1; x++) {
                        for (int z = -1; z < 1; z++) {
                            Bukkit.getServer().getWorld(worldname).getBlockAt(x, secours - 1, z).setType(Material.DIRT);
                        }
                    }
                }

                IsoworldsLogger.severe("2");
            }

            // Téléportation du joueur
            if (player.teleport(go)) {
                player.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION);
                plugin.cooldown.addPlayerCooldown(player, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
            }
        } catch (NullPointerException npe) {
            //
            Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
        }

    }

    private static void buildSafeSpawn(Player player, String worldname, String casualName) {
        Location go = new Location(Bukkit.getServer().getWorld(worldname), 0, 61, 0);

        // Clear zone
        for (int x = -2; x < 2; x++) {
            for (int y = 60; y < 65; y++) {
                for (int z = -2; z < 2; z++) {
                    if (Bukkit.getServer().getWorld(worldname).getBlockAt(x, y, z).getType() != Material.BEDROCK
                            && Bukkit.getServer().getWorld(worldname).getBlockAt(x, y, z).getType() != Material.WALL_SIGN
                            && Bukkit.getServer().getWorld(worldname).getBlockAt(x, y, z).getType() != Material.TORCH) {
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
        Bukkit.getServer().getWorld(worldname).getBlockAt(-1, 61, -2).setType(Material.TORCH);
        Bukkit.getServer().getWorld(worldname).getBlockAt(-2, 61, -2).setType(Material.WALL_SIGN);
        Block block = Bukkit.getServer().getWorld(worldname).getBlockAt(-2, 61, -2);
        BlockState state = block.getState();
        Sign sign = (Sign) state;

        sign.setLine(0, "Bienvenue");
        sign.setLine(1, "Prenez garde...");
        sign.setLine(2, "[" + casualName + "]");
        sign.update();
    }
}
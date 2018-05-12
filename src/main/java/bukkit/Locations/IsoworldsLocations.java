package bukkit.Locations;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsLogger;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by Edwin on 21/10/2017.
 */
public class IsoworldsLocations {

    private static final IsoworldsBukkit plugin = IsoworldsBukkit.instance;

    public static void teleport(Player player, String worldname) {

        // Define dimension name
        if (worldname.equals("end")) {
            worldname = "DIM1";
        } else if (worldname.equals("nether")) {
            worldname = "DIM-1";
        }

        // Construction du point de respawn
        Integer top = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Integer secours;
        Location go = new Location(Bukkit.getServer().getWorld(worldname), 0, 60, 0);

        IsoworldsLogger.severe("Y = " + top);
        try {
            if (top <= 0) {
                IsoworldsLogger.severe("1");
                Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, 61, 0);
            } else {
                IsoworldsLogger.severe("2");
                secours = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, secours, 0);
                if (!go.getBlock().getType().isSolid()) {
                    Bukkit.getServer().getWorld(worldname).getBlockAt(go.getBlockX(), go.getBlockY(), go.getBlockZ()).setType(Material.DIRT);
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
}

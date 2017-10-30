package bukkit.Locations;

import bukkit.Utils.IsoworldsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by Edwin on 21/10/2017.
 */
public class IsoworldsLocations {

    public static void teleport(Player player, String world) {
        Block top = Bukkit.getServer().getWorld(world).getHighestBlockAt(0, 0);
        Location go = new Location (Bukkit.getServer().getWorld(world), 0, 60, 0);
        Integer secours;
        try {
            Double Y = top.getLocation().getY();
            if (Y == null) {
                Bukkit.getServer().getWorld(world).getBlockAt(go).setType(Material.DIRT);
                go = new Location (Bukkit.getServer().getWorld(world), 0, 61, 0);
            } else {
                secours = Bukkit.getServer().getWorld(world).getHighestBlockYAt(0,0);
                go = new Location (Bukkit.getServer().getWorld(world), 0, secours, 0);
            }
        } catch (NullPointerException npe) {
            Bukkit.getServer().getWorld(world).getBlockAt(go).setType(Material.DIRT);
        }

        // Téléportation du joueur
        if (player.teleport(go)) {
            IsoworldsUtils.cm("Le joueur a bien été téléporté !");
        } else {
            IsoworldsUtils.cm("Le joueur n'a pas pu être téléporté !");
        }
    }
}

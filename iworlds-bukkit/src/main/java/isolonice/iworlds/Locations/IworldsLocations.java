package isolonice.iworlds.Locations;

import isolonice.iworlds.Utils.IworldsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created by Edwin on 21/10/2017.
 */
public class IworldsLocations {

    public static void teleport(Player player, String world) {
        Block top = Bukkit.getServer().getWorld(world).getHighestBlockAt(0, 0);
        Location go = new Location (Bukkit.getServer().getWorld(world), 0, 60, 0);

        try {
            Double Y = top.getLocation().getY();
            if (Y == null) {
                Bukkit.getServer().getWorld(world).getBlockAt(go).setType(Material.DIRT);
                }
        } catch (NullPointerException npe) {
            Bukkit.getServer().getWorld(world).getBlockAt(go).setType(Material.DIRT);
        }

        go = new Location (Bukkit.getServer().getWorld(world), 0, 61, 0);

        // Téléportation du joueur
        if (player.teleport(go)) {
            IworldsUtils.cm("Le joueur a bien été téléporté !");
        } else {
            IworldsUtils.cm("Le joueur n'a pas pu être téléporté !");
        }

        IworldsUtils.cm("La position du nouveau spawn est : " + go);
        IworldsUtils.cm("iw " + world + ": Point de respawn du joueur défini sur le spawn du iWorld");

    }
}

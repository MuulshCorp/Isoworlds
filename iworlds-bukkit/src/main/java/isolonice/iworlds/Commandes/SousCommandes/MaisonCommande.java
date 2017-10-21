package isolonice.iworlds.Commandes.SousCommandes;

import isolonice.iworlds.IworldsBukkit;
import isolonice.iworlds.Locations.IworldsLocations;
import isolonice.iworlds.Utils.IworldsUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Edwin on 20/10/2017.
 */
public class MaisonCommande {

    static IworldsBukkit plugin;

    public MaisonCommande(IworldsBukkit instance) {
        this.plugin = instance;
    }
    public static void Maison(CommandSender sender, String[] args) {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) sender;
        final String check_p;
        final String check_w;
        final String CHECK = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";

        worldname = (pPlayer.getUniqueId() + "-iWorld");

        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            check.setString(2, check_w);

            IworldsUtils.cm("CHECK REQUEST: " + check);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                Bukkit.getServer().createWorld(new WorldCreator(worldname));
            }
        } catch (Exception se){
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "CHECK Sijania indique que vous ne possédez aucun iWorld.");
            return;
        }

        // Construction du point de respawn
        Location spawn = Bukkit.getServer().getWorld(worldname).getSpawnLocation();
        Block top = Bukkit.getServer().getWorld(worldname).getHighestBlockAt(0, 0);
        Location secours;
        Location go = new Location (Bukkit.getServer().getWorld(worldname), 0, 60, 0);

        try {
            Double Y = top.getLocation().getY();
            if (Y == null) {
                Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
            }
        } catch (NullPointerException npe) {
            Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
        }

        go = new Location (Bukkit.getServer().getWorld(worldname), 0, 61, 0);

        // Téléportation du joueur
        if (pPlayer.teleport(go)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Bon retour à vous, " + pPlayer.getName());
        } else {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + "Sijania ne parvient pas à vous téléporter, veuillez contacter un membre de l'équipe Isolonice.");;
        }
        return;

    }
}

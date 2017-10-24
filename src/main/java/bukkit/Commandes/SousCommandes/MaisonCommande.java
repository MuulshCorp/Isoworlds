package bukkit.Commandes.SousCommandes;

import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
import common.Msg;
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

    public static IworldsBukkit instance;

    public static void Maison(CommandSender sender, String[] args) {

        instance = IworldsBukkit.getInstance();

        // Variables
        String worldname = "";
        Player pPlayer = (Player) sender;
        String check_p;
        String check_w;
        String CHECK = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        worldname = (pPlayer.getUniqueId() + "-iWorld");

        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            check.setString(2, check_w);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                Bukkit.getServer().createWorld(new WorldCreator(worldname));
            } else {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
                return;
            }
        } catch (Exception se){
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        // Construction du point de respawn
        Integer top = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Integer secours;
        Location go = new Location (Bukkit.getServer().getWorld(worldname), 0, 60, 0);

        try {
            if (top == null) {
                Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
                go = new Location (Bukkit.getServer().getWorld(worldname), 0, 61, 0);
            } else {
                secours = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
                go = new Location (Bukkit.getServer().getWorld(worldname), 0, secours, 0);
            }
        } catch (NullPointerException npe) {
            Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
        }

        // Téléportation du joueur
        if (pPlayer.teleport(go)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION + pPlayer.getName());
        } else {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "Sijania ne parvient pas à vous téléporter, veuillez contacter un membre de l'équipe Isolonice.");;
        }
        return;

    }
}

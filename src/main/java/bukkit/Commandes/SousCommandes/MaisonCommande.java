package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import com.sun.org.apache.xml.internal.serializer.utils.MsgKey;
import common.Msg;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.print.attribute.standard.MediaSize;

/**
 * Created by Edwin on 20/10/2017.
 */
public class MaisonCommande {

    public static IsoworldsBukkit instance;

    public static void Maison(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();

        // Variables
        String worldname = "";
        Player pPlayer = (Player) sender;


        // Check if home of trusted isoworld
        IsoworldsUtils.cm("DEBUG MAISON: " + args.length);
        if (args.length == 1) {
            worldname = (pPlayer.getUniqueId() + "-IsoWorld");
            // Si joueur après maison
        } else if (args.length == 2) {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(args[1]);
            worldname = owner.getUniqueId().toString() + "IsoWorld";
        }

        // SELECT WORLD
        if (!IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        // Construction du point de respawn
        Integer top = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
        Integer secours;
        Location go = new Location(Bukkit.getServer().getWorld(worldname), 0, 60, 0);

        try {
            if (top == null) {
                Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, 61, 0);
            } else {
                secours = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
                go = new Location(Bukkit.getServer().getWorld(worldname), 0, secours, 0);
            }
        } catch (NullPointerException npe) {
            Bukkit.getServer().getWorld(worldname).getBlockAt(go).setType(Material.DIRT);
        }

        // Téléportation du joueur
        if (pPlayer.teleport(go)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TELEPORTATION);
        } else {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania ne parvient pas à vous téléporter, veuillez contacter un membre de l'équipe Isolonice.");
            ;
        }
        return;

    }
}

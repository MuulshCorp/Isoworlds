package bukkit.Commandes.SousCommandes;

import bukkit.Locations.IworldsLocations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Edwin on 20/10/2017.
 */
public class TeleportCommande {

    public static void Teleport(CommandSender sender, String[] args) {

        Player pPlayer = (Player) sender;

        if (args.length < 1 || args.length < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "Veuillez indiquer le joueur cible et le monde cible.");
            return;
        }

        if (!Bukkit.getServer().getPlayer(args[0]).isOnline()) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + "Le joueur indiqué n'est pas connecté, ou vous avez mal entré son pseudonyme.");
            return;
        } else {
            IworldsLocations.teleport(Bukkit.getServer().getPlayer(args[0]), args[1]);
        }
        return;
    }
}

package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Locations.IsoworldsLocations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/10/2017.
 */
public class TeleportCommande {

    public static IsoworldsBukkit instance;

    @SuppressWarnings("deprecation")
    public static void Teleport(CommandSender sender, String[] args) {

        Player pPlayer = (Player) sender;

        if (args.length < 1 || args.length < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Veuillez indiquer le joueur cible et le monde cible.");
            return;
        }

        if (!Bukkit.getServer().getPlayer(args[0]).isOnline()) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Le joueur indiqué n'est pas connecté, ou vous avez mal entré son pseudonyme.");
            return;
        } else {
            IsoworldsLocations.teleport(Bukkit.getServer().getPlayer(args[0]), args[1]);
        }
        return;
    }
}

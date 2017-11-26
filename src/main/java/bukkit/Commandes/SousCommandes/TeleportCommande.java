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

    public static void Teleport(CommandSender sender, String[] args) {

        Player pPlayer = (Player) sender;

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return;
        }

        if (args.length < 1 || args.length < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Veuillez indiquer le joueur cible et le monde cible.");
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        if (!Bukkit.getServer().getPlayer(args[0]).isOnline()) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Le joueur indiqué n'est pas connecté, ou vous avez mal entré son pseudonyme.");
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        } else {
            IsoworldsLocations.teleport(Bukkit.getServer().getPlayer(args[0]), args[1]);
        }
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;
    }
}

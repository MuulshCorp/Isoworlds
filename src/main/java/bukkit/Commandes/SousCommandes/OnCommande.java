package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/10/2017.
 */
public class OnCommande {

    public static IsoworldsBukkit instance;

    public static void On(CommandSender sender, String[] args) {
        // Variables
        String worldname = "";
        Player pPlayer = (Player) sender;
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        IsoworldsUtils.cm("check");

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return;
        }

        if (!IsoworldsUtils.iworldExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Import / Export
        if (!IsoworldsUtils.ieWorld(pPlayer, worldname)) {
            // Suppression lock
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Si le monde n'est pas chargé alors on le fait
        if (Bukkit.getServer().getWorld(worldname) == null) {
            Bukkit.getServer().createWorld(new WorldCreator(worldname));
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: Sijania vient d'activer votre IsoWorld.");
        }

        IsoworldsUtils.cm("finished");
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;
    }
}

package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Locations.IsoworldsLocations;
import bukkit.Utils.IsoworldsLogger;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.Msg;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/10/2017.
 */
public class MaisonCommande {

    public static IsoworldsBukkit instance;

    @SuppressWarnings("deprecation")
    public static void Maison(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();

        // Variables
        String worldname = "";
        Player pPlayer;
        String uuid;
        pPlayer = (Player) sender;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.MAISON)) {
            return;
        }

        worldname = (pPlayer.getUniqueId() + "-IsoWorld");

        // Si la méthode renvoi vrai alors on return car le lock est défini pour l'import, sinon elle le set auto
        if (isLocked(pPlayer, "checkTag")) {
            return;
        }

        // Import / Export
        if (!IsoworldsUtils.checkTag(pPlayer, worldname)) {
            return;
        }

        // Supprime le lock
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");

        // SELECT WORLD (load it if need)
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, true)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        IsoworldsLocations.teleport(pPlayer, worldname);
    }
}

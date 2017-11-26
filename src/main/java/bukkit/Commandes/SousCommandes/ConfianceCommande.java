package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/10/2017.
 */
public class ConfianceCommande {

    public static IsoworldsBukkit instance;

    public static void Confiance(CommandSender sender, String[] args) {

        Player pPlayer = (Player) sender;
        UUID uuidcible;
        Integer len = args.length;

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return;
        }

        // SELECT WORLD
        if (!IsoworldsUtils.iworldExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        if (len > 2 || len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.INVALIDE_JOUEUR);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Getting uuidcible
        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
        } else {
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
        }

        // CHECK AUTORISATIONS
        if (IsoworldsUtils.trustExists(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_TRUST);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // INSERT
        if (!IsoworldsUtils.insertTrust(pPlayer, uuidcible, Msg.keys.SQL)) {
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TRUST);
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;
    }
}

package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class ConfianceCommande {

    public static IsoworldsBukkit instance;

    @SuppressWarnings("deprecation")
    public static void Confiance(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();
        Player pPlayer = (Player) sender;
        UUID uuidcible;
        Integer len = args.length;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.CONFIANCE)) {
            return;
        }

        // SELECT WORLD
        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        if (len > 2 || len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.INVALIDE_JOUEUR);
            return;
        }

        // Getting uuidcible
        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            IsoworldsUtils.cm("ARG: " + args[1]);
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
            IsoworldsUtils.cm("OFFLINE");
        } else {
            IsoworldsUtils.cm("ARG: " + args[1]);
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
            IsoworldsUtils.cm("ONLINE");
        }

        IsoworldsUtils.cm("CONFIANCE: " + uuidcible);

        // CHECK AUTORISATIONS
        if (IsoworldsUtils.isTrusted(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_TRUST);
            return;
        }

        // INSERT
        if (!IsoworldsUtils.setTrust(pPlayer, uuidcible, Msg.keys.SQL)) {
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_TRUST);
        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
    }
}

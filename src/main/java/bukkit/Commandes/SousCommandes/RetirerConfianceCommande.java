package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class RetirerConfianceCommande {

    static final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String REMOVE = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";

    public static IsoworldsBukkit instance;

    @SuppressWarnings("deprecation")
    public static void RetirerConfiance(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();
        // SQL Variables
        Player pPlayer = (Player) sender;
        UUID uuidcible;
        Boolean is;
        Integer len = args.length;

        if (len > 2 || len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.INVALIDE_JOUEUR);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        try {
            // SELECT WORLD
            if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
                return;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
            return;
        }

        // Defining uuidcible
        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            is = false;
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
        } else {
            is = true;
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
        }

        // IF TARGET NOT SET
        if (uuidcible == null) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.INVALIDE_JOUEUR);
            return;
        }

        // DENY SELF REMOVE
        if (uuidcible.toString().equals(pPlayer.getUniqueId().toString())) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.BLUE + Msg.keys.DENY_SELF_REMOVE);
            return;
        }

        // CHECK AUTORISATIONS
        if (!IsoworldsUtils.isTrusted(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_TRUST);
            return;
        }

        // DELETE AUTORISATION
        if (!IsoworldsUtils.deleteTrust(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
            return;
        }

        Location spawn = Bukkit.getServer().getWorld("Isolonice").getSpawnLocation();
        if (is == true) {
            if (Bukkit.getServer().getPlayer(uuidcible).getWorld().getName().equals(pPlayer.getUniqueId().toString() + "-IsoWorld")) {
                Player player = Bukkit.getServer().getPlayer(args[1]);
                player.teleport(spawn);
                player.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.KICK_TRUST);
            }
        } // Gestion du kick offline à gérer dès que possible

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_RETIRER_CONFIANCE);
    }
}

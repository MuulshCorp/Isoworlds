package bukkit.Commandes.SousCommandes;

import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
import com.google.common.base.Charsets;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class RetirerConfianceCommande {

    static final String SELECT = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String REMOVE = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";

    public static IworldsBukkit instance;

    public static void RetirerConfiance(CommandSender sender, String[] args) {

        instance = IworldsBukkit.getInstance();

        // SQL Variables
        final String Suuid_p;
        final String Suuid_w;
        final String Iuuid_p;
        final String Iuuid_w;
        final String check_w;
        final String check_p;

        Player pPlayer = (Player) sender;
        UUID uuidcible;
        Boolean is;
        Integer len = args.length;

        if (len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.INVALIDE_JOUEUR);
            return;
        }

        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            is = false;
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
        } else {
            is = true;
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
        }


        if (uuidcible == null) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.INVALIDE_JOUEUR);
            return;
        }

        try {
            // CHECK AUTORISATIONS
            try {
                PreparedStatement check = instance.database.prepare(CHECK);
                // UUID _P
                check_p = uuidcible.toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = (pPlayer.getUniqueId().toString() + "-iWorld");
                check.setString(2, check_w);
                // Requête
                ResultSet rselect = check.executeQuery();
                if (!rselect.isBeforeFirst() ) {
                    return;
                }

            } catch (Exception se) {
                se.printStackTrace();
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
                return;
            }

            // REMOVE

            if (uuidcible.toString().equals(pPlayer.getUniqueId().toString())) {
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + Msg.keys.DENY_SELF_REMOVE);
                return;
            }

            // SELECT WORLD
            try {
                PreparedStatement select = instance.database.prepare(SELECT);
                // UUID_P
                Suuid_p = pPlayer.getUniqueId().toString();
                select.setString(1, Suuid_p);
                // UUID_W
                Suuid_w = (pPlayer.getUniqueId() + "-iWorld");
                select.setString(2, Suuid_w);
                IworldsUtils.cm("SELECT REQUEST: " + select);
                // Requête
                ResultSet rselect = select.executeQuery();
                if (!rselect.isBeforeFirst() ) {
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
                    return;
                }

            } catch (Exception se) {
                se.printStackTrace();
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
                return;
            }

            try {
                PreparedStatement insert = instance.database.prepare(REMOVE);
                // UUID_P
                Iuuid_p = uuidcible.toString();
                insert.setString(1, Iuuid_p);
                // UUID_W
                Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");
                insert.setString(2, Iuuid_w);
                insert.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        Location spawn = Bukkit.getServer().getWorld("Isolonice").getSpawnLocation();
        if (is == true) {
            Player player = Bukkit.getServer().getPlayer(args[1]);
            player.teleport(spawn);
            player.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.KICK_TRUST);
        } // Gestion du kick offline à gérer dès que possible

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_RETIRER_CONFIANCE);
        return;

    }
}

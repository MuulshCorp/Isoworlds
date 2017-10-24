package bukkit.Commandes.SousCommandes;

import bukkit.IworldsBukkit;
import bukkit.Utils.IworldsUtils;
import com.google.common.base.Charsets;
import common.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class ConfianceCommande {

    static final String SELECT = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
    static final String INSERT = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    static final String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";

    public static IworldsBukkit instance;

    public static void Confiance(CommandSender sender, String[] args) {

        instance = IworldsBukkit.getInstance();

        // SQL Variables
        final String Suuid_p;
        final String Suuid_w;
        final String Iuuid_p;
        final String Iuuid_w;
        final String check_w;
        final String check_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Player pPlayer = (Player) sender;
        UUID uuidcible;
        Integer len = args.length;

        if (len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Messages.getMessage("INVALIDE_JOUEUR"));
            return;
        }

        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
        } else {
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
        }

        try {
            // CHECK AUTORISATIONS
            try {
                PreparedStatement check = instance.database.prepare(CHECK);
                // UUID _P
                check_p = uuidcible.toString();
                check.setString(1, check_p);
                // UUID_W
                check_w = (pPlayer.getUniqueId() + "-iWorld");
                check.setString(2, check_w);
                // Requête
                ResultSet rselect = check.executeQuery();
                if (rselect.isBeforeFirst() ) {
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Messages.getMessage("EXISTE_TRUST"));
                    return;
                }
            } catch (Exception se) {
                se.printStackTrace();
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + Messages.getError("SQL"));
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
                // Requête
                ResultSet rselect = select.executeQuery();
                if (!rselect.isBeforeFirst() ) {
                    pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Messages.getMessage("EXISTE_PAS_IWORLD"));
                    return;
                }
            } catch (Exception se) {
                se.printStackTrace();
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Messages.getError("SQL"));
                return;
            }

            // INSERT
            try {
                PreparedStatement insert = instance.database.prepare(INSERT);
                // UUID_P
                Iuuid_p = uuidcible.toString();
                insert.setString(1, Iuuid_p);
                // UUID_W
                Iuuid_w = ((pPlayer.getUniqueId()) + "-iWorld");
                insert.setString(2, Iuuid_w);
                // Date
                insert.setString(3, (timestamp.toString()));
                insert.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
                pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Messages.getError("SQL"));
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Messages.getError("SQL"));
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + Messages.getMessage("SUCCES_TRUST"));
        return;
    }
}

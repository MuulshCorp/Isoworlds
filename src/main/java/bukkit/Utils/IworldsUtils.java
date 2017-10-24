package bukkit.Utils;

import bukkit.IworldsBukkit;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by Edwin on 20/10/2017.
 */
public class IworldsUtils {

    // Console message
    public static void cm(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

    // Commande console
    public static void cmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    // Check if iworld exists
    public static Boolean iworldExists(Player pPlayer, String messageErreur) {
        IworldsBukkit instance;
        instance = IworldsBukkit.getInstance();
        String CHECK = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;

        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                Bukkit.getServer().createWorld(new WorldCreator(pPlayer.getUniqueId().toString() + "-iWorld"));
                return true;
            }
        } catch (Exception se){
            se.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return false;
    }

    // Check autorisation trust
    public static Boolean trustExists(Player pPlayer, UUID uuidcible, String messageErreur) {
        IworldsBukkit instance;
        instance = IworldsBukkit.getInstance();
        String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // UUID _P
            check_p = uuidcible.toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId() + "-iWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.BLUE + messageErreur);
            return false;
        }
        return false;
    }

    // insert trust
    public static Boolean insertCreation(Player pPlayer, String messageErreur) {
        IworldsBukkit instance;
        instance = IworldsBukkit.getInstance();
        String INSERT = "INSERT INTO `iworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`) VALUES (?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = instance.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-iWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, instance.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // insert trust
    public static Boolean insertTrust(Player pPlayer, UUID uuidcible, String messageErreur) {
        IworldsBukkit instance;
        instance = IworldsBukkit.getInstance();
        String INSERT = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`) VALUES (?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
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
            // Serveur_id
            insert.setString(4, instance.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // delete iworld
    public static Boolean deleteIworld(Player pPlayer, String messageErreur) {
        IworldsBukkit instance;
        instance = IworldsBukkit.getInstance();
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String DELETE_IWORLDS = "DELETE FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = instance.database.prepare(DELETE_IWORLDS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");

            // delete autorisations
            delete_autorisations.setString(1, Iuuid_w);
            delete_autorisations.setString(2, instance.servername);

            // delete iworld
            delete_iworlds.setString(1, Iuuid_p);
            delete_iworlds.setString(2, Iuuid_w);
            delete_iworlds.setString(3, instance.servername);

            // execute
            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // Delete trust
    public static Boolean deleteTrust(Player pPlayer, String messageErreur) {
        IworldsBukkit instance;
        instance = IworldsBukkit.getInstance();
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");

            // delete autorisation
            delete_autorisations.setString(1, Iuuid_p);
            delete_autorisations.setString(2, Iuuid_w);
            delete_autorisations.setString(3, instance.servername);

            // execute
            delete_autorisations.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    public static void getHelp(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "iWorlds " + ChatColor.GOLD + "] ---------------------");
        s.sendMessage(" ");
        s.sendMessage(ChatColor.GOLD + "- Basique: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "||" + ChatColor.GREEN + "iworld" + ChatColor.GOLD + "/" + ChatColor.GREEN + "iworlds");
        s.sendMessage(ChatColor.GOLD + "- Création: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "création"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "créer" + ChatColor.GOLD + "][" + ChatColor.GREEN + "create" + ChatColor.GOLD + "][" + ChatColor.GREEN + "c" + ChatColor.GOLD + "]");
        s.sendMessage(ChatColor.GOLD + "- Refonte: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "refonte"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "refondre" + ChatColor.GOLD + "][" + ChatColor.GREEN + "r" + ChatColor.GOLD + "]");
        s.sendMessage(ChatColor.GOLD + "- Désactivation: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "désactiver"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "off" + ChatColor.GOLD + "][" + ChatColor.GREEN + "décharger" + ChatColor.GOLD + "][" + ChatColor.GREEN + "unload" + ChatColor.GOLD + "]");
        s.sendMessage(ChatColor.GOLD + "- Activation: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "activer"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "charger" + ChatColor.GOLD + "][" + ChatColor.GREEN + "on" + ChatColor.GOLD + "][" + ChatColor.GREEN + "load" + ChatColor.GOLD + "]");
        s.sendMessage(ChatColor.GOLD + "- Confiance: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "trust"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "a" + ChatColor.GOLD + "] <" + ChatColor.GREEN + "pseudo" + ChatColor.GOLD + ">");
        s.sendMessage(ChatColor.GOLD + "- Retirer: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "retirer"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "untrust" + ChatColor.GOLD + "][" + ChatColor.GREEN + "supprimer" + ChatColor.GOLD + "][" + ChatColor.GREEN + "revome"
                + ChatColor.GOLD + "] <" + ChatColor.GREEN + "pseudo" + ChatColor.GOLD + ">");
        s.sendMessage(ChatColor.GOLD + "- Météo: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "météo"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "meteo" + ChatColor.GOLD + "][" + ChatColor.GREEN + "weather" + ChatColor.GOLD + "][" + ChatColor.GREEN + "m" + ChatColor.GOLD + "]");
        s.sendMessage(ChatColor.GOLD + "- Maison: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "maison"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "home" + ChatColor.GOLD + "][" + ChatColor.GREEN + "h" + ChatColor.GOLD + "]");
        s.sendMessage(" ");
    }

}

package bukkit.Utils;

import bukkit.IworldsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
        String CHECK = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        String check_w;
        String check_p;
        instance = IworldsBukkit.getInstance();

        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            check.setString(2, check_w);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                return true;
            }
        } catch (Exception se){
            se.printStackTrace();
            IworldsUtils.cm("[Erreur: 1]: Incident à traiter par les administrateurs");
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return false;
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

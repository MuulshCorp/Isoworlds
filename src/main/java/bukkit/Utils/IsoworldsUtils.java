package bukkit.Utils;

import bukkit.IsoworldsBukkit;
import common.ManageFiles;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edwin on 20/10/2017.
 */
public class IsoworldsUtils {

    private static final IsoworldsBukkit instance = IsoworldsBukkit.getInstance();

    // Console message
    public static void cm(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

    // Commande console
    public static void cmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    // Check if iworld exists
    public static Boolean iworldExists(String uuid, String messageErreur) {
        IsoworldsBukkit instance;
        instance = IsoworldsBukkit.getInstance();
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;

        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _P
            check_p = uuid;
            check.setString(1, check_p);
            // UUID_W
            check_w = (uuid + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst() ) {
                Bukkit.getServer().createWorld(new WorldCreator(uuid + "-IsoWorld"));
                return true;
            }
        } catch (Exception se){
            se.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            return false;
        }
        return false;
    }

    // check if status is push or pull exists
    // true si présent, false si envoyé ou à envoyer
    public static Boolean iworldPushed(String world, String messageErreur) {
        String CHECK = "SELECT STATUS FROM `isoworlds` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID_W
            check_w = world;
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            IsoworldsUtils.cm(check.toString());
            IsoworldsUtils.cm("Debug 8");
            while (rselect.next()) {
                IsoworldsUtils.cm(rselect.toString());
                IsoworldsUtils.cm("Debug 9");
                if (rselect.getInt(1) == 1) {
                    IsoworldsUtils.cm("Debug 10");
                    return true;
                } else {
                    return false;
                }

            }
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return false;
        }
        return false;
    }

    // Cooldown modèle: uuid;commande
    public static Boolean isCooldown(String pPlayer, String command) {
        // Si le tableau est null alors cooldown 0 sinon cooldown 1
        if (instance.cooldown.get(pPlayer + ";" + command) == null) {
            return false;
        } else {
            return true;
        }
    }

    // Vérifie si le cooldown est présent et renvoi vrai, sinon défini le cooldown et renvoi false
    public static Boolean isSetCooldown(Player pPlayer, String className) {
        // Si le cooldown est set, alors on renvoie false avec un message de sorte à stopper la commande et informer le jouer
        if (isCooldown(pPlayer.getUniqueId().toString(), String.class.getName())) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania indique que vous devez patienter avant de pouvoir utiliser de nouveau cette commande.");
            return true;
        } else {
            // On set cooldown
            instance.cooldown.put(pPlayer.getUniqueId().toString() + ";" + className, 1);
            return false;
        }
    }

    // Import Export

    public static Boolean ieWorld(Player pPlayer, String worldname) {
        // Vérification si monde en statut pushed
        if (IsoworldsUtils.iworldPushed(worldname, Msg.keys.SQL)) {
            IsoworldsUtils.cm("Debug 6");
            // Création des chemins pour vérification
            File file = new File(ManageFiles.getPath() + worldname);
            File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");
            // Si Isoworld dossier présent (sans tag), on repasse le status à 0 (présent) et on continue

            if (file.exists()) {
                IsoworldsUtils.cm("Debug 7");
                IsoworldsUtils.iworldSetStatus(worldname, 0, Msg.keys.SQL);
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vient de terminer son travail, l'IsoWorld est disponible !");
                // Si le dossier est en @PULL et qu'un joueur le demande alors on le passe en @PULL
                // Le script check ensutie
                return true;
            } else {
                // Lance la task import/export
                BukkitTask task = new IsoWorldsTasks(pPlayer, file).runTaskTimer(instance, 20, 20);
            }

            if (file2.exists()) {
                IsoworldsUtils.cm("TEST 0");
                ManageFiles.rename(ManageFiles.getPath() + worldname + "@PUSHED", "@PULL");
                IsoworldsUtils.cm("PULL OK");
                return false;
            }
            return false;
        }
        return true;
    }

    // set status
    // true si pushed, false si envoyé ou à envoyer

    public static Boolean iworldSetStatus(String world, Integer status, String messageErreur) {
        String CHECK = "UPDATE `isoworlds` SET `STATUS` = ? WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // STATUS
            check.setInt(1, status);
            // UUID_W
            check_w = (world);
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            IsoworldsUtils.cm("Debug 3: " + check.toString());
            check.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
            IsoworldsUtils.cm(messageErreur);
            return false;
        }
        return false;
    }

    // Check autorisation trust
    public static Boolean trustExists(Player pPlayer, UUID uuidcible, String messageErreur) {
        String CHECK = "SELECT * FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // UUID _P
            check_p = uuidcible.toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (pPlayer.getUniqueId() + "-IsoWorld");
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
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.BLUE + messageErreur);
            return false;
        }
        return false;
    }

    // insert trust
    public static Boolean insertCreation(Player pPlayer, String messageErreur) {
        String INSERT = "INSERT INTO `isoworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`, `SERVEUR_ID`, `STATUS`) VALUES (?, ?, ?, ?, ?)";
        String Iuuid_w;
        String Iuuid_p;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = instance.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, instance.servername);
            // STATUS
            insert.setInt(5, 0);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // insert trust
    public static Boolean insertTrust(Player pPlayer, UUID uuidcible, String messageErreur) {
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
            Iuuid_w = ((pPlayer.getUniqueId()) + "-IsoWorld");
            insert.setString(2, Iuuid_w);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, instance.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // delete iworld
    public static Boolean deleteIworld(Player pPlayer, String messageErreur) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String DELETE_IWORLDS = "DELETE FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = instance.database.prepare(DELETE_IWORLDS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

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
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // Delete trust
    public static Boolean deleteTrust(Player pPlayer, String messageErreur) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            Iuuid_p = pPlayer.getUniqueId().toString();
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-IsoWorld");

            // delete autorisation
            delete_autorisations.setString(1, Iuuid_p);
            delete_autorisations.setString(2, Iuuid_w);
            delete_autorisations.setString(3, instance.servername);

            // execute
            delete_autorisations.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            IsoworldsUtils.cm(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    public static void getHelp(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "IsoWorlds " + ChatColor.GOLD + "] ---------------------");
        s.sendMessage(" ");
        s.sendMessage(ChatColor.GOLD + "- Basique: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "||" + ChatColor.GREEN + "isoworld" + ChatColor.GOLD + "/" + ChatColor.GREEN + "isoworlds");
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
        s.sendMessage(ChatColor.GOLD + "- Biomes: " + ChatColor.AQUA + "/iw " + ChatColor.GOLD + "[" + ChatColor.GREEN + "biome"
                + ChatColor.GOLD + "][" + ChatColor.GREEN + "biomes" + ChatColor.GOLD + "][" + ChatColor.GREEN + "b" + ChatColor.GOLD + "]");
        s.sendMessage(" ");
    }

    public static String centerTitle(String title) {
        String spacer = "";
        int spaces = 27 - ChatColor.stripColor(title).length();
        for (int i = 0; i < spaces; i++) {
            spacer += " ";
        }
        return spacer + title;
    }
}

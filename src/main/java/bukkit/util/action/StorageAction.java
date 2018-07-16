package bukkit.util;

import bukkit.MainBukkit;
import common.ManageFiles;
import common.Msg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Edwin on 16/07/2018.
 */
public class Storage {

    private static final MainBukkit instance = MainBukkit.getInstance();

    // Set global status
    public static Boolean setGlobalStatus(String messageErreur) {
        String CHECK = "UPDATE `isoworlds` SET `STATUS` = 1 WHERE `SERVEUR_ID` = ?";
        String check_w;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // SERVEUR_ID
            check.setString(1, instance.servername);
            // Requête
            check.executeUpdate();
            Utils.cm(check.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Utils.cm(messageErreur);
            return false;
        }
        return true;
    }

    // Set status of IsoWorld (1 for Pushed, 0 for Present)
    // It returns true if pushed, false si envoyé ou à envoyer
    public static Boolean setStatus(String world, Integer status, String messageErreur) {
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
            Utils.cm("Debug 3: " + check.toString());
            check.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
            Utils.cm(messageErreur);
            return false;
        }
        return false;
    }

    // Check tag of pPlayer IsoWorld (@PUSH, @PUSHED, @PULL, @PULLED, @PUSHED@PULL, @PUSHED@PULLED)
    public static Boolean checkTag(Player pPlayer, String worldname) {
        // Vérification si monde en statut pushed
        if (getStatus(worldname, Msg.keys.SQL)) {
            Utils.cm("Debug 6");
            // Création des chemins pour vérification
            File file = new File(ManageFiles.getPath() + worldname);
            File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");
            // Si Isoworld dossier présent (sans tag), on repasse le status à 0 (présent) et on continue

            if (file.exists()) {
                Utils.cm("Debug 7");
                setStatus(worldname, 0, Msg.keys.SQL);
                // Si le dossier est en @PULL et qu'un joueur le demande alors on le passe en @PULL
                // Le script check ensutie
                return true;
            } else {
                // Lance la task import/export
                BukkitTask task = new PullTask(pPlayer, file).runTaskTimer(instance, 20, 20);
            }

            if (file2.exists()) {
                Utils.cm("TEST 0");
                ManageFiles.rename(ManageFiles.getPath() + worldname + "@PUSHED", "@PULL");
                Utils.cm("PULL OK");
                return false;
            }
            return false;
        }
        return true;
    }

    // Check status of a IsoWorld, if is Pushed return true, else return false
    public static Boolean getStatus(String world, String messageErreur) {
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
            Utils.cm(check.toString());
            Utils.cm("Debug 8");
            while (rselect.next()) {
                Utils.cm(rselect.toString());
                Utils.cm("Debug 9");
                if (rselect.getInt(1) == 1) {
                    Utils.cm("Debug 10");
                    return true;
                } else {
                    return false;
                }

            }
        } catch (Exception se) {
            se.printStackTrace();
            Utils.cm(messageErreur);
            return false;
        }
        return false;
    }

}

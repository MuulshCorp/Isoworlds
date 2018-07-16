package bukkit.util;

import bukkit.MainBukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Edwin on 16/07/2018.
 */
public class PlayTime {

    private static final MainBukkit instance = MainBukkit.getInstance();

    // Ajoute une minute au compteur de temps du joueur
    public static Boolean updatePlayTime(Player pPlayer, String messageErreur) {
        String CHECK = "UPDATE `players_info` SET `playtimes` = `playtimes` + 1 WHERE `UUID_P` = ?";
        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID_P
            check.setString(1, pPlayer.getUniqueId().toString());
            // Requête
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            Utils.cm(messageErreur);
            return false;
        }
    }

    // Get charge of a player
    public static Integer getPlayTime(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `playtimes` FROM `players_info` WHERE `UUID_P` = ?";
        ResultSet result;
        Integer number;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // UUID _P
            check.setString(1, pPlayer.getUniqueId().toString());
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            Utils.cm(messageErreur);
            return null;
        }
        return 0;
    }
}

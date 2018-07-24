package common.action;

import common.MainInterface;
import common.Manager;
import sponge.util.console.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayTimeAction {

    private static final MainInterface instance = Manager.getInstance();

    // Add playertime to player
    public static Boolean updatePlayTime(String playeruuid) {
        String CHECK = "UPDATE `players_info` SET `playtimes` = `playtimes` + 1 WHERE `uuid_p` = ?";
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // Player uuid
            check.setString(1, playeruuid);
            // Request
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
    }

    // Get charge of a player
    public static Integer getPlayTime(String playeruuid) {
        String CHECK = "SELECT `playtimes` FROM `players_info` WHERE `uuid_p` = ?";
        Integer number;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // Player uuid
            check.setString(1, playeruuid);
            // Reqest
            ResultSet rselect = check.executeQuery();
            if (rselect.next()) {
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        return 0;
    }
}

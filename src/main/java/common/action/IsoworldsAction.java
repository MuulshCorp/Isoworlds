package common.action;

import common.Manager;
import common.Mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IsoworldsAction {

    private static final Mysql database = Manager.getInstance().getMysql();
    private static final String servername = Manager.getInstance().getServername();

    // Delete IsoWorld
    public static Boolean deleteIsoWorld(String playeruuid) {
        String Iuuid_p;
        String Iuuid_w;
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `uuid_w` = ? AND `server_id` = ?";
        String DELETE_IWORLDS = "DELETE FROM `isoworlds` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        try {
            PreparedStatement delete_autorisations = database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = database.prepare(DELETE_IWORLDS);
            Iuuid_p = playeruuid;
            Iuuid_w = (playeruuid + "-IsoWorld");

            // delete autorisations
            delete_autorisations.setString(1, Iuuid_w);
            delete_autorisations.setString(2, servername);

            // delete isoworld
            delete_iworlds.setString(1, Iuuid_p);
            delete_iworlds.setString(2, Iuuid_w);
            delete_iworlds.setString(3, servername);

            // execute
            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Used for construction, check if isoworld is in database (don't care charged or not)
    public static Boolean iwExists(String playeruuid) {
        String CHECK = "SELECT * FROM `isoworlds` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = database.prepare(CHECK);
            // Player uuid
            check_p = playeruuid;
            check.setString(1, check_p);
            // Worldname
            check_w = playeruuid + "-IsoWorld";
            check.setString(2, check_w);
            // Server id
            check.setString(3, servername);
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
        return false;
    }
}
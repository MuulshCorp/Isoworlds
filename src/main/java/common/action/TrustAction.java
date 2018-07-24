/*
 * This file is part of IsoWorlds, licensed under the MIT License (MIT).
 *
 * Copyright (c) Edwin Petremann <https://github.com/Isolonice/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package common.action;

import common.MainInterface;
import common.Manager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TrustAction {

    private static final MainInterface instance = Manager.getInstance();

    // Get all isoworlds allowed for a player
    public static ResultSet getAccess(String playeruuid) {
        String CHECK = "SELECT `uuid_w` FROM `autorisations` WHERE `uuid_p` = ? AND `server_id` = ?";
        ResultSet result = null;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            check.setString(1, playeruuid);
            // Server id
            check.setString(2, instance.servername);
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                result = rselect;
                return result;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        return null;
    }

    // Get all trusted players of pPlayer's IsoWorld
    public static ResultSet getTrusts(String worldname) {
        String CHECK = "SELECT `uuid_p` FROM `autorisations` WHERE `uuid_w` = ? AND `server_id` = ?";
        ResultSet result = null;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // World name
            if (!worldname.contains("-IsoWorld")) {
                worldname = (worldname + "-IsoWorld");
            }
            check.setString(1, worldname);
            // Server id
            check.setString(2, instance.servername);
            // Request
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                result = rselect;
                return result;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        return null;
    }

    // Add playeruuid to worldname trusts
    public static Boolean setTrust(String worldname, String playeruuid) {
        String INSERT = "INSERT INTO `autorisations` (`uuid_p`, `uuid_w`, `date_time`, `server_id`) VALUES (?, ?, ?, ?)";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement insert = instance.database.prepare(INSERT);
            // Player uuid
            insert.setString(1, playeruuid);
            // World name
            if (!worldname.contains("-IsoWorld")) {
                worldname = (worldname + "-IsoWorld");
            }
            insert.setString(2, worldname);
            // Date
            insert.setString(3, (timestamp.toString()));
            // Serveur_id
            insert.setString(4, instance.servername);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Delete playeruuid from worldname trusts
    public static Boolean deleteTrust(String worldname, String playeruuid) {
        String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        try {
            PreparedStatement delete_autorisations = instance.database.prepare(DELETE_AUTORISATIONS);
            // World name
            if (!worldname.contains("-IsoWorld")) {
                worldname = (worldname + "-IsoWorld");
            }

            // delete autorisation
            delete_autorisations.setString(1, playeruuid);
            delete_autorisations.setString(2, worldname);
            delete_autorisations.setString(3, instance.servername);

            // execute
            delete_autorisations.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // Check if playeruuid is trusted on worldname
    public static Boolean isTrusted(String worldname, String playeruuid) {
        String CHECK = "SELECT * FROM `autorisations` WHERE `uuid_p` = ? AND `uuid_w` = ? AND `server_id` = ?";
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // Player uuid
            check.setString(1, playeruuid);
            // World name
            if (!worldname.contains("-IsoWorld")) {
                worldname = (worldname + "-IsoWorld");
            }
            check.setString(2, worldname);
            // Server id
            check.setString(3, instance.servername);
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
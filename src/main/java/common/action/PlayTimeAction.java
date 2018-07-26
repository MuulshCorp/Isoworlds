/*
 * This file is part of Isoworlds, licensed under the MIT License (MIT).
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

import common.Manager;
import common.Mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayTimeAction {

    private static final Mysql database = Manager.getInstance().getMysql();

    // Add playertime to player
    public static Boolean updatePlayTime(String playeruuid) {
        String CHECK = "UPDATE `players_info` SET `playtimes` = `playtimes` + 1 WHERE `uuid_p` = ?";
        try {
            PreparedStatement check = database.prepare(CHECK);
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
            PreparedStatement check = database.prepare(CHECK);
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

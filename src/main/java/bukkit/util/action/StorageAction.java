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
package bukkit.util.action;

import bukkit.Main;
import bukkit.util.console.Logger;
import bukkit.util.task.SAS.Pull;
import common.ManageFiles;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StorageAction {

    private static final Main instance = Main.getInstance();

    // Set global status
    public static Boolean setGlobalStatus() {
        String CHECK = "UPDATE `isoworlds` SET `status` = 1 WHERE `server_id` = ?";
        String check_w;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // SERVEUR_ID
            check.setString(1, instance.servername);
            // Requête
            check.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Set status of Isoworld (1 for Pushed, 0 for Present)
    // It returns true if pushed, false si envoyé ou à envoyer
    public static Boolean setStatus(String world, Integer status) {
        String CHECK = "UPDATE `isoworlds` SET `status` = ? WHERE `uuid_w` = ? AND `server_id` = ?";
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
            check.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
        return false;
    }

    // Check tag of pPlayer Isoworld (@PUSH, @PUSHED, @PULL, @PULLED, @PUSHED@PULL, @PUSHED@PULLED)
    public static Boolean checkTag(Player pPlayer, String worldname) {
        // Setting path
        File file = new File(ManageFiles.getPath() + worldname);
        File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");

        // If 1 (Pushed) in database
        if (StorageAction.getStatus(worldname)) {
            // Safe check for both tag and untag folders, removing untag
            if (file.exists() & file2.exists()) {
                ManageFiles.deleteDir(file);
            }

            // Starting pull process
            if (file2.exists()) {
                ManageFiles.rename(ManageFiles.getPath() + worldname + "@PUSHED", "@PULL");
                new Pull(pPlayer, file).runTaskTimer(instance, 20, 20);
                return false;
            }

            // End pull process (Isoworld should be successfully pulled)
            if (file.exists()) {
                StorageAction.setStatus(worldname, 0);
                return true;
            }

            // Else we return false, something wrong
            return false;
        } else {
            // If 0 (Pulled) in database

            // Safe check for both tag and untag folders
            // We remove the untag one because the pull process seems to be unfinished
            // Then we set the Isoworld to 1 (Pushed), player will redo the command and it will pull it
            if (file.exists() & file2.exists()) {
                ManageFiles.deleteDir(file);
                StorageAction.setStatus(worldname, 1);
                return false;
            }

            // If folder untag exists them everying seems ok, we return true;
            if (file.exists()) {
                return true;
            }
        }

        // Return false if not 1 or 0 in database, something wrong happened
        return false;
    }

    // Check status of a Isoworld, if is Pushed return true, else return false
    public static Boolean getStatus(String world) {
        String CHECK = "SELECT status FROM `isoworlds` WHERE `uuid_w` = ? AND `server_id` = ?";
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
            if (rselect.next()) {
                return (rselect.getInt(1) == 1 ? true : false);
            }
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
        return false;
    }

}

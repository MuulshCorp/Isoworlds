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
package bukkit.util.action;

import bukkit.MainBukkit;
import bukkit.util.console.Command;
import bukkit.util.console.Logger;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;

public class IsoWorldsAction {

    private static final MainBukkit instance = MainBukkit.getInstance();

    // Create IsoWorld for pPlayer
    public static Boolean setIsoWorld(Player pPlayer, String messageErreur) {
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
            Logger.severe(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // Delete IsoWorld of pPlayer
    public static Boolean deleteIsoWorld(Player pPlayer, String messageErreur) {
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
            Logger.severe(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + messageErreur);
            return false;
        }
        return true;
    }

    // Create world properties IsoWorlds
    public static void setWorldProperties(String worldname, Player pPlayer) {

        // Properties of IsoWorld
        UUID player;
        World world = Bukkit.getServer().getWorld(worldname);
        // Radius border 500
        int x;
        int y;
        if (pPlayer.hasPermission("isoworlds.size.1000")) {
            x = 1000;
            y = 1000;
            // Radius border 750
        } else if (pPlayer.hasPermission("isoworlds.size.750")) {
            x = 750;
            y = 750;
            // Radius border 1000
        } else if (pPlayer.hasPermission("isoworlds.size.500")) {
            x = 500;
            y = 500;
            // Radius border default
        } else {
            x = 250;
            y = 250;
        }

        Logger.severe("Size: " + x + " " + y);
        Command.sendCmd("wb " + worldname + " set " + x + " " + y + " 0 0");

        if (world != null) {
            Block yLoc = world.getHighestBlockAt(0, 0);
            world.setPVP(true);
            world.setSpawnLocation(0, yLoc.getY(), 0);
            world.setGameRuleValue("MobGriefing", "false");
        }
        Logger.info("WorldProperties à jour");
    }

    // Check if iworld exists
    public static Boolean isPresent(Player pPlayer, String messageErreur, Boolean load) {
        MainBukkit instance;
        instance = MainBukkit.getInstance();
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;

        try {
            PreparedStatement check = instance.database.prepare(CHECK);

            // UUID _P
            check_p = pPlayer.getUniqueId().toString();
            check.setString(1, check_p);
            // UUID_W
            check_w = (check_p + "-IsoWorld");
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête

            ResultSet rselect = check.executeQuery();

            if (rselect.isBeforeFirst()) {
                // Chargement si load = true
                if (!StorageAction.getStatus(check_p + "-IsoWorld", Msg.keys.SQL)) {
                    if (load) {
                        Bukkit.getServer().createWorld(new WorldCreator(check_p + "-IsoWorld"));
                    }
                }
                setWorldProperties(check_p + "-IsoWorld", pPlayer);
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            return false;
        }
        return false;
    }

    // Used for construction, check if isoworld is in database (don't care charged or not)
    public static Boolean iwExists(String uuid, String messageErreur) {
        String CHECK = "SELECT * FROM `isoworlds` WHERE `UUID_P` = ? AND `UUID_W` = ? AND `SERVEUR_ID` = ?";
        String check_w;
        String check_p;
        try {
            PreparedStatement check = instance.database.prepare(CHECK);
            // UUID _P
            check_p = uuid;
            check.setString(1, check_p);
            // UUID_W
            check_w = uuid + "-IsoWorld";
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, instance.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.isBeforeFirst()) {
                return true;
            }
        } catch (Exception se) {
            se.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            return false;
        }
        return false;
    }
}

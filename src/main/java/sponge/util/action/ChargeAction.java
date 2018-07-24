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
package sponge.util.action;

import common.Msg;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.Main;
import sponge.util.console.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChargeAction {

    public static final Main plugin = Main.instance;

    // Get charge of a player
    public static Integer getCharge(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `uuid_p` = ?";
        ResultSet result;
        Integer number;
        // If unlimited
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return 1;
        }
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);
            // UUID _P
            check.setString(1, pPlayer.getUniqueId().toString());
            // Requête
            ResultSet rselect = check.executeQuery();
            if (rselect.next()) {
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            bukkit.util.console.Logger.severe(messageErreur);
            return null;
        }
        initCharges(pPlayer, Msg.keys.SQL);
        return 0;
    }

    // Vérifie les charges, retire si en possède sinon return false avec message
    public static Integer checkCharge(Player pPlayer, String messageErreur) {
        Integer charges = getCharge(pPlayer, Msg.keys.SQL);
        Integer newCharges;

        if (charges == null) {
            initCharges(pPlayer, Msg.keys.SQL);
            return -1;
        }
        // Permissions unlimited for player
        if (pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            return -99;
        }
        if (charges <= 0) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que vous ne possédez aucune charge !").color(TextColors.RED))).build()));
            return -1;
        }
        return charges;
    }

    // Ajoute des charges à un joueur, succès = true
    public static Boolean updateCharge(Player pPlayer, Integer number, String messageErreur) {
        String CHECK = "UPDATE `players_info` SET `charges` = ? WHERE `uuid_p` = ?";
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // Number
            check.setInt(1, number);
            // UUID_P
            check.setString(2, pPlayer.getUniqueId().toString());
            // Requête
            Logger.info("Debug 3: " + check.toString());
            check.executeUpdate();
            return true;
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
    }

    // Get charge of a player
    public static Integer firstTime(Player pPlayer, String messageErreur) {
        String CHECK = "SELECT `charges` FROM `players_info` WHERE `uuid_p` = ?";
        ResultSet result;
        Integer number;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);
            // UUID _P
            check.setString(1, pPlayer.getUniqueId().toString());
            // Requête
            ResultSet rselect = check.executeQuery();
            while (rselect.next()) {
                Logger.info(rselect.toString());
                Logger.info("Debug charge 1");
                number = rselect.getInt(1);
                return number;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
        return null;
    }

    // Init charges and playtime on first connect
    public static Boolean initCharges(Player pPlayer, String messageErreur) {
        String INSERT = "INSERT INTO `players_info` (`uuid_p`, `charges`, `playtimes`) VALUES (?, ?, ?)";
        Integer number;
        String Iuuid_p;

        try {
            PreparedStatement insert = plugin.database.prepare(INSERT);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            insert.setString(1, Iuuid_p);
            // Number
            number = 0;
            insert.setInt(2, number);
            // PlayTime
            insert.setInt(3, number);
            insert.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
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
package bukkit.command.sub;

import bukkit.Main;
import bukkit.util.console.Logger;
import bukkit.util.message.Message;
import common.Msg;
import common.action.IsoWorldsAction;
import common.action.TrustAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Untrust {

    public static Main instance;

    @SuppressWarnings("deprecation")
    public static void RetirerConfiance(CommandSender sender, String[] args) {
        instance = Main.getInstance();
        Player pPlayer = (Player) sender;
        UUID uuidcible;
        Boolean is;
        Integer len = args.length;

        if (len > 2 || len < 2) {
            pPlayer.sendMessage(Message.error(Msg.keys.INVALID_PLAYER));
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        try {
            // SELECT WORLD
            if (!IsoWorldsAction.isPresent(pPlayer, false)) {
                return;
            }
        } catch (Exception se) {
            se.printStackTrace();
            return;
        }

        // Defining uuidcible
        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            is = false;
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
        } else {
            is = true;
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
        }

        // IF TARGET NOT SET
        if (uuidcible == null) {
            pPlayer.sendMessage(Message.error(Msg.keys.INVALID_PLAYER));
            return;
        }

        // DENY SELF REMOVE
        if (uuidcible.toString().equals(pPlayer.getUniqueId().toString())) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.BLUE + Msg.keys.DENY_SELF_REMOVE);
            return;
        }

        // CHECK AUTORISATIONS
        if (!TrustAction.isTrusted(uuidcible.toString(), pPlayer.getUniqueId().toString())) {
            pPlayer.sendMessage(Message.error(Msg.keys.NOT_TRUSTED));
            return;
        }

        // DELETE AUTORISATION
        if (!TrustAction.deleteTrust(pPlayer.getUniqueId().toString(), uuidcible.toString())) {
            return;
        }

        Location spawn = Bukkit.getServer().getWorld("Isolonice").getSpawnLocation();
        if (is == true) {
            if (Bukkit.getServer().getPlayer(uuidcible).getWorld().getName().equals(pPlayer.getUniqueId().toString() + "-IsoWorld")) {
                Player player = Bukkit.getServer().getPlayer(args[1]);
                player.teleport(spawn);
                pPlayer.sendMessage(Message.error(Msg.keys.KICK_TRUST));
            }
        } // Gestion du kick offline à gérer dès que possible

        pPlayer.sendMessage(Message.success(Msg.keys.SUCCESS_UNTRUST));
    }
}

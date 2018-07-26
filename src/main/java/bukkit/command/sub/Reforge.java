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
package bukkit.command.sub;

import bukkit.Main;
import bukkit.util.message.Message;
import common.Cooldown;
import common.ManageFiles;
import common.Msg;
import common.action.IsoworldsAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Reforge {

    private final static Map<String, Timestamp> confirm = new HashMap<>();
    public static Main instance;

    public static void Refonte(CommandSender sender, String[] args) {
        String fullpath = "";
        String worldname;
        Player pPlayer = (Player) sender;
        instance = Main.getInstance();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.REFONTE)) {
            return;
        }

        // Check is IsoWorld exists in database
        if (!bukkit.util.action.IsoworldsAction.isPresent(pPlayer, false)) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldNotFound")));
            return;
        }

        // Confirmation message (2 times cmd)
        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("Confirm")));
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            return;
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if (minutes >= 1) {
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(Message.error(Msg.msgNode.get("Confirm")));
                return;
            }
        }

        confirm.remove(pPlayer.getUniqueId().toString());

        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        File destDir = new File(ManageFiles.getPath() + "/Isoworlds-REFONTE/" + worldname);
        destDir.mkdir();

        if (Bukkit.getServer().getWorld(worldname) == null) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldNotFound")));
            return;
        }

        if (Bukkit.getServer().getWorld(worldname) != null) {
            Collection<Player> colPlayers = Bukkit.getServer().getWorld(worldname).getPlayers();
            Integer maxY = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
            Location overworld = new Location(Bukkit.getServer().getWorld("Isolonice"), 0, maxY, 0);
            for (Player player : colPlayers) {
                player.teleport(overworld);
                pPlayer.sendMessage(Message.error(Msg.msgNode.get("ReforgeKick")));
            }
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(worldname), true);
        }

        // Deleting process
        File remove = new File((ManageFiles.getPath() + worldname));
        ManageFiles.deleteDir(remove);
        if (!IsoworldsAction.deleteIsoWorld(pPlayer.getUniqueId().toString())) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("FailReforgeIsoworld")));
            return;
        }

        pPlayer.sendMessage(Message.success(Msg.msgNode.get("SuccesReforge")));

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.REFONTE, Cooldown.REFONTE_DELAY);

        // Open menu to player
        pPlayer.performCommand("iw");
    }
}

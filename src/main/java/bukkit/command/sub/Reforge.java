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

import bukkit.MainBukkit;
import bukkit.util.action.IsoWorldsAction;
import common.Cooldown;
import common.ManageFiles;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
    public static MainBukkit instance;

    public static void Refonte(CommandSender sender, String[] args) {
        // Variables
        String fullpath = "";
        String worldname;
        Player pPlayer = (Player) sender;
        instance = MainBukkit.getInstance();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.REFONTE)) {
            return;
        }

        // SELECT WORLD
        if (!IsoWorldsAction.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            return;
        }

        // Confirmation
        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            return;
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if (minutes >= 1) {
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
                return;
            }
        }

        confirm.remove(pPlayer.getUniqueId().toString());

        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-IsoWorld");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        File sourceDir = new File(ManageFiles.getPath() + worldname);
        File destDir = new File(ManageFiles.getPath() + "/IsoWorlds-REFONTE/" + worldname);
        destDir.mkdir();

        if (Bukkit.getServer().getWorld(worldname) == null) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        if (Bukkit.getServer().getWorld(worldname) != null) {
            Collection<Player> colPlayers = Bukkit.getServer().getWorld(worldname).getPlayers();
            Integer maxY = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
            Location overworld = new Location(Bukkit.getServer().getWorld("Isolonice"), 0, maxY, 0);
            for (Player player : colPlayers) {
                player.teleport(overworld);
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.REFONTE_KICK);
            }
            World world = Bukkit.getServer().getWorld(worldname);
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(worldname), true);
        }

        //iWorldsUtils.deleteDir(sourceDir);
        File remove = new File((ManageFiles.getPath() + worldname));
        ManageFiles.deleteDir(remove);

        // DELETE WORLD
        if (!IsoWorldsAction.deleteIsoWorld(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.KICK_TRUST);
        //Start the lock for this command
        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.REFONTE, Cooldown.REFONTE_DELAY);
        pPlayer.performCommand("iw");
    }
}

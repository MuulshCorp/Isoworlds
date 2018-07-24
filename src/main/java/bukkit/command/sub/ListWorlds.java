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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class ListWorlds {

    public static Main instance;

    public static void Liste(CommandSender sender, String[] args) {

        instance = Main.getInstance();
        Player pPlayer = (Player) sender;
        ArrayList<World> worlds = new ArrayList<World>();
        Boolean check = false;

        for (World world : Bukkit.getServer().getWorlds()) {
            if (world.getName() != null) {
                if (world.getName().contains("-IsoWorld")) {
                    worlds.add(world);
                }
            }
        }

        if (check == true) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania ne repère aucun IsoWorld dans le Royaume Isolonice");
            return;
        }
        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "[Liste des IsoWorlds]");
        for (World w : worlds) {
            String[] split = w.getName().split("-IsoWorld");
            UUID uuid = UUID.fromString(split[0]);
            Player player = Bukkit.getServer().getPlayer(uuid);
            String pname;
            String status;
            if (Bukkit.getPlayer(pPlayer.getUniqueId()) != null) {
                pname = player.getName();
            } else {
                pname = "OFF";
            }
            if (Bukkit.getPlayer(pPlayer.getUniqueId()) != null) {
                status = "ON";
            } else {
                status = "OFF";
            }

            int numOfEntities = w.getEntities().size();
            int loadedChunks = (w.getLoadedChunks().length);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + pname + " [" + status + "] | Chunks: " + loadedChunks + " | Entités: " + numOfEntities);
        }
    }
}

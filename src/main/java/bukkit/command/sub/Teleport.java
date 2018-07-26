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
import bukkit.location.Locations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Teleport {

    public static Main instance;

    @SuppressWarnings("deprecation")
    public static void Teleport(CommandSender sender, String[] args) {

        instance = Main.getInstance();
        Player pPlayer = (Player) sender;

        if (args.length < 1 || args.length < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "[Isoworlds]: " + ChatColor.AQUA + "Veuillez indiquer le joueur cible et le monde cible.");
            return;
        }

        if (!Bukkit.getServer().getPlayer(args[0]).isOnline()) {
            pPlayer.sendMessage(ChatColor.GOLD + "[Isoworlds]: " + ChatColor.AQUA + "Le joueur indiqué n'est pas connecté, ou vous avez mal entré son pseudonyme.");
            return;
        } else {
            Locations.teleport(Bukkit.getServer().getPlayer(args[0]), args[1]);
        }
    }
}

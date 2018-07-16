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

import bukkit.util.action.ChargeAction;
import bukkit.util.action.IsoWorldsAction;
import bukkit.util.console.Logger;
import common.Cooldown;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Time {

    public static MainBukkit instance;

    public static void Time(CommandSender sender, String[] args) {

        instance = MainBukkit.getInstance();
        Player pPlayer = (Player) sender;
        Integer len = args.length;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.TIME)) {
            return;
        }

        // If got charges
        int charges = ChargeAction.checkCharge(pPlayer, Msg.keys.SQL);
        if (charges == -1) {
            return;
        }

        if (!IsoWorldsAction.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]" + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        if (len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "IsoWorlds " + ChatColor.GOLD + "] ---------------------");
            pPlayer.sendMessage(" ");
            pPlayer.sendMessage(ChatColor.AQUA + "Sijania vous propose deux temps:");
            pPlayer.sendMessage(ChatColor.GOLD + "- Jour: " + ChatColor.AQUA + "/iw time " + ChatColor.GOLD + "[" + ChatColor.GREEN + "jour"
                    + ChatColor.GOLD + "/" + ChatColor.GREEN + "nuit" + ChatColor.GOLD + "]");
            pPlayer.sendMessage(" ");
            return;
        } else {
            World weather = Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld");
            Logger.tracking("Time world: " + weather.getName());
            if (args[1].equals("jour") || args[1].equals("day")) {
                weather.setTime(0);
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania vient de changer le temps de votre IsoWorld.");
                return;
            } else if (args[1].equals("nuit") || args[1].equals("night")) {
                weather.setTime(12000);
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania vient de changer la météo de votre IsoWorld.");
                return;
            }

            if (!pPlayer.hasPermission("isoworlds.unlimited.charges")) {
                ChargeAction.updateCharge(pPlayer, charges - 1, Msg.keys.SQL);
            }
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Vous venez d'utiliser une charge, nouveau compte: " + ChatColor.GREEN + (charges - 1) + " charge(s)");

            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.TIME, Cooldown.TIME_DELAY);
        }
    }
}

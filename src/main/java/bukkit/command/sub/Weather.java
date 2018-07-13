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

import common.Cooldown;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import bukkit.util.Utils;

public class Weather {

    public static MainBukkit instance;

    public static void Meteo(CommandSender sender, String[] args) {

        instance = MainBukkit.getInstance();
        int num;
        Player pPlayer = (Player) sender;
        Integer len = args.length;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.METEO)) {
            return;
        }

        // If got charges
        int charges = Utils.checkCharge(pPlayer, Msg.keys.SQL);
        if (charges == -1) {
            return;
        }

        if (!Utils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]" + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        if (len < 3) {
            pPlayer.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "IsoWorlds " + ChatColor.GOLD + "] ---------------------");
            pPlayer.sendMessage(" ");
            pPlayer.sendMessage(ChatColor.AQUA + "Sijania vous propose trois types de météo:");
            pPlayer.sendMessage(ChatColor.GOLD + "- Pluie: " + ChatColor.AQUA + "/iw meteo " + ChatColor.GOLD + "[" + ChatColor.GREEN + "pluie"
                    + ChatColor.GOLD + "/" + ChatColor.GREEN + "soleil" + ChatColor.GOLD + "] " + ChatColor.GREEN + "(durée en minute)");
            pPlayer.sendMessage(" ");
            return;
        } else {
            try {
                num = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania indique que vous n'avez pas renseigné de minutes.");
                return;
            }
            World weather = Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld");
            Utils.cm("Weather world: " + weather.getName());
            if (args[1].equals("pluie") || args[1].equals("rain")) {
                weather.setStorm(true);
                weather.setWeatherDuration(num);
            } else if (args[1].equals("soleil") || args[1].equals("sun")) {
                weather.setStorm(false);
                weather.setWeatherDuration(num);
                // Message pour tous les joueurs
            }
            for (Player p : Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld").getPlayers()) {
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds] Sijania indique que " + pPlayer.getName()
                        + " vient de changer la météo à: " + args[1] + " pendant: " + num + " ticks.");
            }
        }

        if (!pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            Utils.updateCharge(pPlayer, charges - 1, Msg.keys.SQL);
        }
        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Vous venez d'utiliser une charge, nouveau compte: " + ChatColor.GREEN + (charges - 1) + " charge(s)");

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.METEO, Cooldown.METEO_DELAY);
    }
}

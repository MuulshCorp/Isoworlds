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

import bukkit.util.console.Logger;
import bukkit.util.message.Message;
import common.Cooldown;
import common.Msg;
import common.action.ChargeAction;
import common.action.TrustAction;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Time {

    public static Main instance;

    public static void Time(CommandSender sender, String[] args) {

        instance = Main.getInstance();

        Player pPlayer = (Player) sender;
        String worldname = (pPlayer.getUniqueId().toString() + "-Isoworld");
        Integer len = args.length;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.TIME)) {
            return;
        }

        // If got charges
        int charges = ChargeAction.checkCharge(pPlayer);
        if (charges == -1) {
            return;
        }

        // Check if actual world is an Isoworld
        if (!pPlayer.getWorld().getName().contains("-Isoworld")) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("NotInAIsoworld")));
            return;
        }

        // Check if player is trusted
        if (!TrustAction.isTrusted(pPlayer.getUniqueId().toString(), pPlayer.getWorld().getName())) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("NotTrusted")));
            return;
        }

        if (len < 2) {
            pPlayer.sendMessage(Message.success(Msg.msgNode.get("HeaderIsoworld")));
            pPlayer.sendMessage(Message.success(Msg.msgNode.get("SpaceLine")));
            pPlayer.sendMessage(Message.success (Msg.msgNode.get("TimeTypes")));
            pPlayer.sendMessage(Message.success(Msg.msgNode.get("TimeTypesDetail")));
            pPlayer.sendMessage(Message.success(Msg.msgNode.get("SpaceLine")));
            return;
        } else {
            World time = pPlayer.getWorld();
            Logger.tracking("Time world: " + time.getName());
            if (args[1].equals("jour") || args[1].equals("day")) {
                time.setTime(0);
            } else if (args[1].equals("nuit") || args[1].equals("night")) {
                time.setTime(12000);
            }

            // Send message to all players
            for (Player p : pPlayer.getWorld().getPlayers()) {
                p.sendMessage(Message.success(Msg.msgNode.get("TimeChangeSuccess") + " " + pPlayer.getName()));
            }

            if (!pPlayer.hasPermission("Isoworlds.unlimited.charges")) {
                ChargeAction.updateCharge(pPlayer.getUniqueId().toString(), charges - 1);
            }

            pPlayer.sendMessage(Message.success(Msg.msgNode.get("ChargeUsed")));

            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.TIME, Cooldown.TIME_DELAY);
        }
    }
}

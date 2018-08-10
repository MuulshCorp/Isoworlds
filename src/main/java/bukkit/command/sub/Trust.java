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
import common.Msg;
import common.action.IsoworldsAction;
import common.action.TrustAction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Trust {

    public static Main instance;

    @SuppressWarnings("deprecation")
    public static void Confiance(CommandSender sender, String[] args) {

        instance = Main.getInstance();
        Player pPlayer = (Player) sender;
        UUID uuidcible;
        Integer len = args.length;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.CONFIANCE)) {
            return;
        }

        if (len > 2 || len < 2) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("InvalidPlayer")));
            return;
        }

        // Check if world exists
        if (!bukkit.util.action.IsoworldsAction.isPresent(pPlayer, false)) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldNotFound")));
            return;
        }

        // Getting uuidcible
        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
        } else {
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
        }

        // CHECK AUTORISATIONS
        if (TrustAction.isTrusted(uuidcible.toString(), pPlayer.getUniqueId().toString())) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("AlreadyTrusted")));
            return;
        }

        // INSERT
        if (!TrustAction.setTrust(pPlayer.getUniqueId().toString(), uuidcible.toString())) {
            return;
        }

        pPlayer.sendMessage(Message.success(Msg.msgNode.get("SuccessTrust")));

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
    }
}

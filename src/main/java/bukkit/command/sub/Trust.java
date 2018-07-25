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
import bukkit.util.message.Message;
import common.Cooldown;
import common.Msg;
import common.action.IsoWorldsAction;
import common.action.TrustAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            pPlayer.sendMessage(Message.error(Msg.keys.INVALID_PLAYER));
            return;
        }

        // Check if world exists
        if (!IsoWorldsAction.isPresent(pPlayer, false)) {
            pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_NOT_FOUND));
            return;
        }

        // Getting uuidcible
        if (Bukkit.getServer().getPlayer(args[1]) == null) {
            uuidcible = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId();
        } else {
            uuidcible = Bukkit.getServer().getPlayer(args[1]).getUniqueId();
        }

        // CHECK AUTORISATIONS
        if (TrustAction.isTrusted(pPlayer.getUniqueId().toString(), uuidcible.toString())) {
            pPlayer.sendMessage(Message.error(Msg.keys.ALREADY_TRUSTED));
            return;
        }

        // INSERT
        if (!TrustAction.setTrust(pPlayer.getUniqueId().toString(), uuidcible.toString())) {
            return;
        }

        pPlayer.sendMessage(Message.success(Msg.keys.SUCCESS_TRUST));

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
    }
}

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
import bukkit.location.Locations;
import bukkit.util.action.LockAction;
import bukkit.util.action.StorageAction;
import bukkit.util.message.Message;
import common.Cooldown;
import common.Msg;
import common.action.IsoWorldsAction;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home {

    public static final Main instance = Main.instance;

    @SuppressWarnings("deprecation")
    public static void Maison(CommandSender sender, String[] args) {
        String worldname = "";
        Player pPlayer = (Player) sender;
        worldname = (pPlayer.getUniqueId() + "-IsoWorld");

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.MAISON)) {
            return;
        }

        // If return true then lock is enabled for import, else setting it
        if (LockAction.isLocked(pPlayer, "checkTag")) {
            return;
        }

        // Pull / Push
        // False if processing on isoworld as @PUSHED state in database
        // True if IsoWorld avalable
        if (!StorageAction.checkTag(pPlayer, worldname)) {
            return;
        }

        // Removing lock
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");

        // Check if IsoWorld exists and load it if needed (true)
        if (!IsoWorldsAction.isPresent(pPlayer, true)) {
            pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_NOT_FOUND));
            return;
        }

        Locations.teleport(pPlayer, worldname);
    }
}

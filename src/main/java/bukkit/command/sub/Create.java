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

import bukkit.util.console.Logger;
import bukkit.util.message.Message;
import common.ManageFiles;
import bukkit.Main;
import bukkit.location.Locations;
import common.Msg;
import common.action.IsoWorldsAction;
import common.action.TrustAction;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Create {

    public static void Creation(CommandSender sender, String[] args) {
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;
        Integer len = args.length;

        // Check if isoworld exists in database
        if (IsoWorldsAction.isPresent(pPlayer, false)) {
            pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_ALREADY_EXISTS));
            return;
        }

        // Create message
        pPlayer.sendMessage(Message.success(Msg.keys.CREATING_ISOWORLD);

        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-IsoWorld/");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");

        // Check if isoworld exists
        if (Bukkit.getServer().getWorld(worldname) != null) {
            pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_ALREADY_EXISTS));
            return;
        }

        // Check arg lenght en send patern types message
        if (len < 2) {
            pPlayer.sendMessage(Message.error(Msg.keys.HEADER_ISOWORLD));
            pPlayer.sendMessage(Message.error(Msg.keys.SPACE_LINE));
            pPlayer.sendMessage(Message.error(Msg.keys.PATERN_TYPES));
            pPlayer.sendMessage(Message.error(Msg.keys.PATERN_TYPES_DETAIL));
            pPlayer.sendMessage(Message.error(Msg.keys.SPACE_LINE));
            return;
        }

        Logger.tracking(args[1]);
        File sourceFile;
        switch (args[1]) {
            case ("n"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                break;
            case ("v"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                break;
            case ("o"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                break;
            case ("f"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                break;
            default:
                return;
        }

        // Bukkit doesn't remove folder with delete method, we do it manually (sponge deleting itself)
        File destFile = new File(fullpath);
        File deleteFile = new File(fullpath + "region");

        try {
            Bukkit.getServer().createWorld(new WorldCreator(worldname));
        } catch (Exception ie) {
            ie.printStackTrace();
        }

        // Remove - unload - copy - load
        Bukkit.getServer().unloadWorld(worldname, true);
        ManageFiles.deleteDir(deleteFile);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            ie.printStackTrace();
            return;
        }

        Bukkit.getServer().createWorld(new WorldCreator(worldname));

        if (!IsoWorldsAction.setIsoWorld(pPlayer.getUniqueId().toString())) {
            return;
        }

        if (!TrustAction.setTrust(pPlayer.getUniqueId().toString(), pPlayer.getUniqueId().toString())) {
            return;
        }

        Locations.teleport(pPlayer, worldname);

        // Set delayed world properties as WB doesn't know the newly created iw
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> Bukkit.getScheduler().runTask(Main.instance, () -> {
            IsoWorldsAction.setWorldProperties(pPlayer.getDisplayName() + "-IsoWorld", pPlayer);
        }), 60);

        pPlayer.sendMessage(Message.success(Msg.keys.ISOWORLD_SUCCESS_CREATE));
        pPlayer.sendMessage(Message.success(Msg.keys.WELCOME_1) + Msg.keys.WELCOME_2);
    }
}

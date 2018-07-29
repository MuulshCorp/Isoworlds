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

import bukkit.util.console.Logger;
import bukkit.util.message.Message;
import common.ManageFiles;
import bukkit.Main;
import bukkit.location.Locations;
import common.Msg;
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

        // Check if Isoworld exists in database
        if (bukkit.util.action.IsoworldsAction.isPresent(pPlayer, false)) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldAlreadyExists")));
            return;
        }

        // Create message
        pPlayer.sendMessage(Message.success(Msg.msgNode.get("CreatingIsoworld")));

        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-Isoworld/");
        worldname = (pPlayer.getUniqueId().toString() + "-Isoworld");

        // Check if Isoworld exists
        if (Bukkit.getServer().getWorld(worldname) != null) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldAlreadyExists")));
            return;
        }

        // Check arg lenght en send patern types message
        if (len < 2) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("HeaderIsoworld")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("SpaceLine")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("PaternTypes")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("PaternTypesDetail")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("SpaceLine")));
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

        if (!bukkit.util.action.IsoworldsAction.setIsoworld(pPlayer)) {
            return;
        }

        if (!TrustAction.setTrust(pPlayer.getUniqueId().toString(), pPlayer.getUniqueId().toString())) {
            return;
        }

        Locations.teleport(pPlayer, worldname);

        // Set delayed world properties as WB doesn't know the newly created iw
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> Bukkit.getScheduler().runTask(Main.instance, () -> {
            bukkit.util.action.IsoworldsAction.setWorldProperties(pPlayer.getDisplayName() + "-Isoworld", pPlayer);
        }), 60);

        pPlayer.sendMessage(Message.success(Msg.msgNode.get("IsoworldsuccessCreate")));
        pPlayer.sendMessage(Message.success(Msg.msgNode.get("Welcome1)" + Msg.msgNode.get("Welcome2"))));
    }
}

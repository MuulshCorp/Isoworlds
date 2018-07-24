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

import bukkit.util.action.IsoWorldsAction;
import bukkit.util.action.TrustAction;
import bukkit.util.console.Logger;
import common.ManageFiles;
import bukkit.Main;
import bukkit.location.Locations;
import common.Msg;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class Create {

    static Main instance;

    public static void Creation(CommandSender sender, String[] args) {

        // Variables
        instance = Main.getInstance();
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) sender;
        Integer len = args.length;

        // SELECT WORLD
        if (IsoWorldsAction.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-IsoWorld/");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        // Check si le monde existe déjà
        if (Bukkit.getServer().getWorld(worldname) != null) {
            Logger.warning("Le monde existe déjà");
            return;
        }

        // Vérifie le nb argument
        if (len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "IsoWorlds " + ChatColor.GOLD + "] ---------------------");
            pPlayer.sendMessage(" ");
            pPlayer.sendMessage(ChatColor.AQUA + "Sijania vous propose 4 types de IsoWorld:");
            pPlayer.sendMessage(ChatColor.GOLD + "- FLAT/OCEAN/NORMAL/VOID: " + ChatColor.AQUA + "/iw creation " + ChatColor.GOLD + "[" + ChatColor.GREEN + "[TYPE]");
            pPlayer.sendMessage(" ");
            return;
        }

        Logger.tracking(args[1]);
        File deleteFile = new File(fullpath + "region");
        File sourceFile;
        switch (args[1]) {
            case ("n"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                Logger.tracking("PATERN NORMAL: " + pPlayer.getName());
                break;
            case ("v"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                Logger.tracking("PATERN VOID: " + pPlayer.getName());
                break;
            case ("o"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                Logger.tracking("PATERN OCEAN: " + pPlayer.getName());
                break;
            case ("f"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN/");
                Logger.tracking("PATERN FLAT: " + pPlayer.getName());
                break;
            default:
                return;
        }

        File destFile = new File(fullpath);

        try {
            Bukkit.getServer().createWorld(new WorldCreator(worldname));
        } catch (Exception ie) {
            ie.printStackTrace();
            Logger.severe(Msg.keys.SQL);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SQL);
        }

        // Remove - unload - copy - load
        Bukkit.getServer().unloadWorld(worldname, true);
        ManageFiles.deleteDir(deleteFile);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            Logger.severe(Msg.keys.FICHIERS);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.FICHIERS);
            return;
        }

        Bukkit.getServer().createWorld(new WorldCreator(worldname));

        // INSERT
        if (!IsoWorldsAction.setIsoWorld(pPlayer, Msg.keys.SQL)) {
            return;
        }

        // INSERT TRUST
        if (!TrustAction.setTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
            return;
        }

        Locations.teleport(pPlayer, worldname);

        // Set delayed world properties as WB doesn't know the newly created iw
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> Bukkit.getScheduler().runTask(Main.instance, () -> {
            IsoWorldsAction.setWorldProperties(pPlayer.getDisplayName() + "-IsoWorld", pPlayer);
        }), 60);

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.SUCCES_CREATION_1);
    }
}

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
package sponge.command.sub;

import sponge.util.message.Message;
import common.Msg;
import common.action.IsoworldsAction;
import common.action.TrustAction;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.location.Locations;
import common.ManageFiles;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import sponge.util.action.StatAction;
import sponge.util.console.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.util.List;
import java.util.Optional;

public class Create implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;

        // Check if Isoworld exists in database
        if (sponge.util.action.IsoworldsAction.isPresent(pPlayer, false)) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldAlreadyExists")));
            return CommandResult.success();
        }

        // Create message
        pPlayer.sendMessage(Message.success(Msg.msgNode.get("CreatingIsoworld")));

        fullpath = (ManageFiles.getPath() + StatAction.PlayerToUUID(pPlayer) + "-Isoworld");
        worldname = (pPlayer.getUniqueId().toString() + "-Isoworld");
        String[] arg = args.split(" ");
        int size = arg.length;

        // Check properties exists
        if (Sponge.getServer().getWorldProperties(worldname).isPresent()) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldAlreadyExists")));
            return CommandResult.success();
        }

        // Check arg lenght en send patern types message
        if (size < 1) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("HeaderIsoworld")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("SpaceLine")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("PaternTypes")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("PaternTypesDetail")));
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("SpaceLine")));
            return CommandResult.success();
        }

        File sourceFile;
        switch (arg[0]) {
            case ("n"):
                sourceFile = new File(ManageFiles.getPath() + "Isoworlds-UTILS/Isoworlds-PATERN/");
                break;
            case ("v"):
                sourceFile = new File(ManageFiles.getPath() + "Isoworlds-UTILS/Isoworlds-PATERN-V/");
                break;
            case ("o"):
                sourceFile = new File(ManageFiles.getPath() + "Isoworlds-UTILS/Isoworlds-PATERN-O/");
                break;
            case ("f"):
                sourceFile = new File(ManageFiles.getPath() + "Isoworlds-UTILS/Isoworlds-PATERN-F/");
                break;
            default:
                return CommandResult.success();
        }

        File destFile = new File(fullpath);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            ie.printStackTrace();
            return CommandResult.success();
        }

        //  Create world properties
        sponge.util.action.IsoworldsAction.setWorldProperties(worldname, pPlayer);

        if (sponge.util.action.IsoworldsAction.setIsoworld(pPlayer)) {
            if (TrustAction.setTrust(pPlayer.getUniqueId().toString(), pPlayer.getUniqueId().toString())) {
                // Loading
                Sponge.getGame().getServer().loadWorld(worldname);

                pPlayer.sendMessage(Message.success(Msg.msgNode.get("IsoworldsuccessCreate")));

                // Teleport
                Locations.teleport(pPlayer, worldname);

                // Welcome title (only sponge)
                pPlayer.sendTitle(Logger.titleSubtitle(Msg.msgNode.get("Welcome1") + pPlayer.getName(), Msg.msgNode.get("Welcome2")));
            }
        }
        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return null;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return null;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return null;
    }
}
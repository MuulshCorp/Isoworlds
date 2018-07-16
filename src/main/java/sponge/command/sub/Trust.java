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
package sponge.command.sub;

import common.Cooldown;
import common.Msg;
import sponge.MainSponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.util.action.IsoWorldsAction;
import sponge.util.action.TrustAction;

import javax.annotation.Nullable;
import java.util.*;

public class Trust implements CommandCallable {

    private final MainSponge plugin = MainSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        UUID uuidcible;
        Player pPlayer = (Player) source;
        String[] arg = args.split(" ");
        int size = arg.length;

        //If the method return true then the command is in lock
        if (!plugin.cooldown.isAvailable(pPlayer, Cooldown.CONFIANCE)) {
            return CommandResult.success();
        }

        if (size > 1) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.INVALIDE_JOUEUR).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // SELECT WORLD
        if (!IsoWorldsAction.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        try {
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            Optional<User> player = userStorage.get(arg[0]);
            try {
                uuidcible = player.get().getUniqueId();
            } catch (NoSuchElementException e){
                e.printStackTrace();
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.INVALIDE_JOUEUR).color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }

            if (uuidcible.toString().isEmpty() || (size > 1)) {
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.INVALIDE_JOUEUR).color(TextColors.AQUA))).build()));
                // Suppression lock
                return CommandResult.success();
            }
        } catch (NoSuchElementException | IllegalArgumentException i) {
            i.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.DATA).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // CHECK AUTORISATIONS
        if (TrustAction.isTrusted(pPlayer, uuidcible, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_TRUST).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // INSERT
        if (!TrustAction.setTrust(pPlayer, uuidcible, Msg.keys.SQL)) {
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + "confiance");
            return CommandResult.success();
        }

        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(Msg.keys.SUCCES_TRUST).color(TextColors.AQUA))).build()));
        plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.CONFIANCE, Cooldown.CONFIANCE_DELAY);
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
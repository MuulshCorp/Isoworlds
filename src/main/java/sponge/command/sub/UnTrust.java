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

import common.Msg;
import common.action.IsoWorldsAction;
import common.action.TrustAction;
import sponge.Main;

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
import sponge.util.message.Message;

import javax.annotation.Nullable;

import java.util.*;

public class UnTrust implements CommandCallable {

    private final Main plugin = Main.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        UUID uuidcible;
        Player pPlayer = (Player) source;
        String[] arg = args.split(" ");
        int size = arg.length;
        Optional<User> player;

        if (size > 1) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.INVALIDE_JOUEUR).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // SELECT WORLD
        if (!IsoWorldsAction.isPresent(pPlayer, false)) {
            pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_NOT_FOUND));
            return CommandResult.success();
        }

        try {
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            player = userStorage.get(arg[0]);
            try {
                uuidcible = player.get().getUniqueId();
            } catch (NoSuchElementException e){
                e.printStackTrace();
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }

            if (uuidcible.toString().isEmpty() || (size > 1)) {
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.INVALIDE_JOUEUR).color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        } catch (NoSuchElementException | IllegalArgumentException i) {
            i.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // CHECK AUTORISATIONS
        if (!TrustAction.isTrusted(pPlayer.getUniqueId().toString(), uuidcible.toString())) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_TRUST_2).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // DELETE AUTORISATION
        if (!TrustAction.deleteTrust(pPlayer.getUniqueId().toString(), uuidcible.toString())) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        try {
            if (player.get().isOnline()) {
                if (player.get().getPlayer().get().getWorld().getName().equals(pPlayer.getUniqueId().toString() + "-IsoWorld")) {
                    Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
                    Player playerOnline = Sponge.getServer().getPlayer(arg[0]).get();
                    playerOnline.setLocation(spawn);
                    playerOnline.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.KICK_TRUST).color(TextColors.AQUA))).build()));
                }
            }
        } catch (NoSuchElementException nse) {
            nse.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.DATA).color(TextColors.AQUA))).build()));

        }

        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(Msg.keys.SUCCES_RETIRER_CONFIANCE).color(TextColors.AQUA))).build()));
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
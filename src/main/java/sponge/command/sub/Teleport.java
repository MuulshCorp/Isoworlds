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

import common.Msg;
import common.action.IsoworldsAction;
import org.spongepowered.api.text.format.TextColors;
import sponge.location.Locations;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.util.action.LockAction;
import sponge.util.message.Message;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


public class Teleport implements CommandCallable {

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        Player pPlayer = (Player) source;
        int length = 0;
        String[] arg = args.split(" ");
        length = arg.length;

        // Check if world exists
        if (!sponge.util.action.IsoworldsAction.isPresent(pPlayer, true)) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("IsoworldNotFound")));
            return CommandResult.success();
        }

        // Check if is world is loaded
        if (!(Sponge.getServer().getWorld(pPlayer.getUniqueId().toString() + "-Isoworld").get().isLoaded())) {
            pPlayer.sendMessage(Text.of(Text.builder("[Isoworlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que votre Isoworld doit être chargé pour en changer le temps.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (LockAction.isLocked(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        if (length != 2) {
            pPlayer.sendMessage(Message.error(Msg.msgNode.get("InvalidPlayer")));
            return CommandResult.success();
        }

        Optional<Player> target = Sponge.getServer().getPlayer(arg[0]);
        Player player;
        if (!target.isPresent()) {
            Text message = Text.of("Le joueur indiqué n'est pas connecté, ou vous avez mal entré son pseudonyme.");
            pPlayer.sendMessage(message);
            return CommandResult.success();
        } else {
            player = target.get().getPlayer().get();
            Locations.teleport(player, arg[1]);
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
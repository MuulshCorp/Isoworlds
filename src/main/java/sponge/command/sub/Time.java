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
import common.action.ChargeAction;
import org.spongepowered.api.text.action.TextActions;
import sponge.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.util.action.IsoWorldsAction;
import sponge.util.action.StatAction;
import sponge.util.message.Message;

import javax.annotation.Nullable;
import java.util.*;

public class Time implements CommandCallable {

    private final Main plugin = Main.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        // SQL Variables
        Player pPlayer = (Player) source;
        String worldname = (StatAction.PlayerToUUID(pPlayer) + "-IsoWorld");
        String[] arg = args.split(" ");
        int size = arg.length;

        //If the method return true then the command is in lock
        if (!plugin.cooldown.isAvailable(pPlayer, Cooldown.TIME)) {
            return CommandResult.success();
        }

        // If got charges
        int charges = ChargeAction.checkCharge(pPlayer);
        if (charges == -1) {
            return CommandResult.success();
        }

        // Check if world exists
        if (!IsoWorldsAction.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_NOT_FOUND));
            return CommandResult.success();
        }

        // Check if is world is loaded
        if (!Sponge.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld").isPresent()) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que votre IsoWorld doit être chargé pour en changer le temps.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        if (size == 0) {
            pPlayer.sendMessage(Text.of(Text.builder("--------------------- [ ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("IsoWorlds ").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] ---------------------").color(TextColors.GOLD)))
                    .build()));

            pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

            // Soleil
            Text meteo = Text.of(Text.builder("Sijania vous propose deux temps:").color(TextColors.AQUA).build());
            pPlayer.sendMessage(meteo);

            // Jour
            Text day = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Jour").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Day").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]")))
                    .onClick(TextActions.runCommand("/iw time jour " + worldname)).build());
            pPlayer.sendMessage(day);

            // Nuit
            Text night = Text.of(Text.builder("- [Temps: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Nuit").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("/").color(TextColors.GOLD)))
                    .append(Text.of(Text.builder("Night").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("]")))
                    .onClick(TextActions.runCommand("/iw time nuit " + worldname)).build());
            pPlayer.sendMessage(night);

            pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

            return CommandResult.success();
        } else if (size == 1) {
            if (arg[0].equals("jour") || arg[0].equals("day")) {
                Sponge.getServer().getWorld(worldname).get().getProperties().setWorldTime(0);
            } else if (arg[0].equals("nuit") || arg[0].equals("night")) {
                Sponge.getServer().getWorld(worldname).get().getProperties().setWorldTime(12000);
            }
        } else {
            return CommandResult.success();
        }

        // Message pour tous les joueurs
        for (Player p : Sponge.getServer().getWorld(worldname).get().getPlayers()) {
            p.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania indique que " + pPlayer.getName() + " vient de changer la temps à: " + arg[0])
                    .color(TextColors.GOLD).build()));
        }

        if (!pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            ChargeAction.updateCharge(pPlayer.getUniqueId().toString(), charges - 1);
        }
        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Vous venez d'utiliser une charge, nouveau compte: ").color(TextColors.RED)
                        .append(Text.of(Text.builder(charges - 1 + " charge(s)").color(TextColors.GREEN))))).build()));

        plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.TIME, Cooldown.TIME_DELAY);
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
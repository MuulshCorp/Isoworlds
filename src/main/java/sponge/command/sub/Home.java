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
import common.action.IsoWorldsAction;
import sponge.Main;
import sponge.location.Locations;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.util.action.LockAction;
import sponge.util.action.StatAction;
import sponge.util.action.StorageAction;
import sponge.util.message.Message;

public class Home implements CommandExecutor {

    private final Main plugin = Main.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) source;
        worldname = (StatAction.PlayerToUUID(pPlayer) + "-IsoWorld");

        //If the method return true then the command is in lock
        if (!plugin.cooldown.isAvailable(pPlayer, Cooldown.MAISON)) {
            return CommandResult.success();
        }

        // Si la méthode renvoi vrai alors on return car le lock est défini pour l'import, sinon elle le set auto
        if (LockAction.isLocked(pPlayer, "checkTag")) {
            return CommandResult.success();
        }

        // Import / Export
        // Faux si une procédure est en cours sur un IsoWorld en était @PUSHED en BDD
        // Vrai si IsoWorld disponible
        if (!StorageAction.checkTag(pPlayer, worldname)) {
            return CommandResult.success();
        }

        // Supprime le lock
        plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");

        // SELECT WORLD (load if need)
        if (!IsoWorldsAction.isPresent(pPlayer, true)) {
            pPlayer.sendMessage(Message.error(Msg.keys.ISOWORLD_NOT_FOUND));
            return CommandResult.success();
        }

        // Construction du point de respawn

        // Téléportation du joueur
        if (Locations.teleport(pPlayer, worldname)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SUCCES_TELEPORTATION + pPlayer.getName()).color(TextColors.AQUA))).build()));
        } else {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania ne parvient pas à vous téléporter, veuillez contacter un membre de l'équipe Isolonice.").color(TextColors.AQUA))).build()));
        }

        plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.MAISON, Cooldown.MAISON_DELAY);

        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour retourner dans son iWorld"))
                .permission("isoworlds.maison")
                .executor(new Home())
                .build();
    }
}
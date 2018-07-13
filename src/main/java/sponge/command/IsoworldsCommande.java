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
package sponge.command;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import sponge.MainSponge;

import sponge.command.sub.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import sponge.util.Inventories;

public class IsoworldsCommande implements CommandExecutor {

    private final MainSponge plugin = MainSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        Player pPlayer = (Player) source;

        //IsoworldsUtils.getHelp(pPlayer);
        // Ouverture du menu principal
        pPlayer.openInventory(Inventories.menuPrincipal(pPlayer),  Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande IsoWorlds, permet de créer/refondre/lister"))
                .permission("isoworlds.default")
                .child(new Create(), "creation", "créer", "creer", "create", "c")
                .child(ListWorlds.getCommand(), "lister", "liste", "list", "l")
                .child(Reforge.getCommand(), "refonte", "refondre", "r")
                .child(Home.getCommand(), "maison", "home", "h")
                .child(new Warp(), "warp", "w")
                .child(new Biome(), "biome", "b")
                .child(new Trust(), "confiance", "trust", "a")
                .child(new UnTrust(), "retirer", "supprimer", "untrust", "remove")
                .child(new Weather(), "meteo", "weather", "m", "météo")
                .child(new Time(), "time", "temps", "t", "cycle")
                .executor(new IsoworldsCommande())
                .build();
    }
}

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
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import sponge.Main;
import sponge.util.action.ChargeAction;
import sponge.util.action.IsoWorldsAction;
import sponge.util.console.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class Biome implements CommandCallable {

    private final Main plugin = Main.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {
        Player pPlayer = (Player) source;
        World world = pPlayer.getWorld();
        String[] arg = args.split(" ");
        BiomeType biome;

        Logger.info(arg[0]);

        //If the method return true then the command is in lock
        if (!plugin.cooldown.isAvailable(pPlayer, Cooldown.BIOME)) {
            return CommandResult.success();
        }

        // If got charges
        int charges = ChargeAction.checkCharge(pPlayer, Msg.keys.SQL);
        if (charges == -1) {
            return CommandResult.success();
        }

        // SELECT WORLD
        if (!IsoWorldsAction.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Check if is in isoworld
        if (!world.getName().equals(pPlayer.getUniqueId() + "-IsoWorld")) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que vous devez être présent dans votre monde pour changer de biome.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        switch (arg[0]) {
            case "plaines":
                biome = BiomeTypes.PLAINS;
                break;
            case "desert":
                biome = BiomeTypes.DESERT;
                break;
            case "marais":
                biome = BiomeTypes.SWAMPLAND;
                break;
            case "océan":
                biome = BiomeTypes.OCEAN;
                break;
            case "champignon":
                biome = BiomeTypes.MUSHROOM_ISLAND;
                break;
            case "jungle":
                biome = BiomeTypes.JUNGLE;
                break;
            case "enfer":
                biome = BiomeTypes.HELL;
                break;
            case "end":
                biome = BiomeTypes.VOID;
                break;
            default:
                return CommandResult.success();
        }

        // Définition biome
        Location loc = pPlayer.getLocation();
        Logger.info("LOCATION " + loc);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                loc.getExtent().setBiome(
                        loc.getChunkPosition().getX() * 16 + x,
                        0,
                        loc.getChunkPosition().getZ() * 16 + z,
                        biome
                );
            }
        }

        if (!pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            ChargeAction.updateCharge(pPlayer, charges - 1, Msg.keys.SQL);
        }
        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Vous venez d'utiliser une charge, nouveau compte: ").color(TextColors.RED)
                        .append(Text.of(Text.builder(charges - 1 + " charge(s)").color(TextColors.GREEN))))).build()));

        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Sijania vient de changer le biome du chunk dans lequel vous êtes. (F9 ou F3 + G)").color(TextColors.AQUA))).build()));

        plugin.cooldown.addPlayerCooldown(pPlayer, Cooldown.BIOME, Cooldown.BIOME_DELAY);

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
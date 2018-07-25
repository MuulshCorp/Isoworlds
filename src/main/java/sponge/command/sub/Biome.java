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
import common.action.TrustAction;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import sponge.Main;
import sponge.util.console.Logger;
import sponge.util.message.Message;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class Biome implements CommandCallable {

    private final Main instance = Main.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {
        Player pPlayer = (Player) source;
        String[] arg = args.split(" ");
        BiomeType biome;

        Logger.info(arg[0]);

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.BIOME)) {
            return CommandResult.success();
        }

        // If got charges
        int charges = ChargeAction.checkCharge(pPlayer);
        if (charges == -1) {
            return CommandResult.success();
        }

        // Check if actual world is an isoworld
        if (!pPlayer.getWorld().getName().contains("-IsoWorld")) {
            pPlayer.sendMessage(Message.error(Msg.keys.NOT_IN_A_ISOWORLD));
        }

        // Check if player is trusted
        if (!TrustAction.isTrusted(pPlayer.getUniqueId().toString(), pPlayer.getWorld().getName())) {
            pPlayer.sendMessage(Message.error(Msg.keys.NOT_TRUSTED));
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

        // Setup every blocks of chunk to the clicked biome
        Location loc = pPlayer.getLocation();
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
            ChargeAction.updateCharge(pPlayer.getUniqueId().toString(), charges - 1);
        }

        pPlayer.sendMessage(Message.success(Msg.keys.CHARGE_USED));
        pPlayer.sendMessage(Message.success(Msg.keys.BIOME_CHANGED));

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.BIOME, Cooldown.BIOME_DELAY);

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
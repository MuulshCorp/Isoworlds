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
package bukkit.command.sub;

import bukkit.Main;
import bukkit.util.message.Message;
import common.Cooldown;
import common.Msg;
import common.action.ChargeAction;
import common.action.TrustAction;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static common.Msg.msgNode;

public class Biome {

    public static Main instance;

    public static void Biome(CommandSender sender, String[] args) {
        instance = Main.getInstance();
        Player pPlayer = (Player) sender;
        Integer len = args.length;
        org.bukkit.block.Biome biome;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.BIOME)) {
            return;
        }

        // If got charges
        int charges = ChargeAction.checkCharge(pPlayer);
        if (charges == -1) {
            return;
        }

        // Check if actual world is an Isoworld
        if (!pPlayer.getWorld().getName().contains("-Isoworld")) {
            pPlayer.sendMessage(Message.error(msgNode.get("NotInAIsoworld")));
            return;
        }

        // Check lenght arg
        if (len < 1) {
            pPlayer.sendMessage(Message.error(msgNode.get("BiomeNotFound")));
            return;
        }

        // Check if player is trusted
        if (!TrustAction.isTrusted(pPlayer.getUniqueId().toString(), pPlayer.getWorld().getName())) {
            pPlayer.sendMessage(Message.error(msgNode.get("NotTrusted")));
            return;
        }

        switch (args[1]) {
            case "plaines":
                biome = org.bukkit.block.Biome.PLAINS;
                break;
            case "desert":
                biome = org.bukkit.block.Biome.DESERT;
                break;
            case "marais":
                biome = org.bukkit.block.Biome.SWAMPLAND;
                break;
            case "océan":
                biome = org.bukkit.block.Biome.OCEAN;
                break;
            case "champignon":
                biome = org.bukkit.block.Biome.MUSHROOM_ISLAND;
                break;
            case "jungle":
                biome = org.bukkit.block.Biome.JUNGLE;
                break;
            case "enfer":
                biome = org.bukkit.block.Biome.HELL;
                break;
            // Biome VOID not in 1.7.10
            default:
                pPlayer.sendMessage(Message.error(msgNode.get("BiomeNotFound")));
                return;
        }

        // Setup every blocks of chunk to the clicked biome
        Chunk chunk = pPlayer.getLocation().getChunk();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final Block block = chunk.getBlock(x, 0, z);
                block.setBiome(biome);
            }
        }

        if (!pPlayer.hasPermission("Isoworlds.unlimited.charges")) {
            ChargeAction.updateCharge(pPlayer.getUniqueId().toString(), charges - 1);
        }

        pPlayer.sendMessage(Message.success(msgNode.get("ChargeUsed")));
        pPlayer.sendMessage(Message.success(msgNode.get("BiomeChanged")));

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.BIOME, Cooldown.BIOME_DELAY);
    }
}
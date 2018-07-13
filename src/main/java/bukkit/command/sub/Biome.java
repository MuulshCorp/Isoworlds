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
package bukkit.command.sub;

import bukkit.MainBukkit;
import bukkit.util.Utils;
import common.Cooldown;
import common.Msg;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Biome {

    public static MainBukkit instance;

    public static void Biome(CommandSender sender, String[] args) {
        // Variables
        instance = MainBukkit.getInstance();
        Player pPlayer = (Player) sender;
        Utils.cm("check");
        Integer len = args.length;
        org.bukkit.block.Biome biome;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.BIOME)) {
            return;
        }

        // If got charges
        int charges = Utils.checkCharge(pPlayer, Msg.keys.SQL);
        if (charges == -1) {
            return;
        }

        // SELECT WORLD
        if (!Utils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            return;
        }

        // Vérification taille args et retour si biome non indiqué
        if (len < 1) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vous requiert un nom de biome dans votre commande.");
            return;
        }
        // On boucle sur les blocks du chunk du joueur et si le biome est défini on stop, sinon on regarde
        // si le biome indiqué existe et on l'applique
        for (String s : args) {
            Utils.cm(s);
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
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Sijania indique que ce biome n'existe pas.");
                return;
        }

        Chunk chunk = pPlayer.getLocation().getChunk();
        Utils.cm("Biomes" + org.bukkit.block.Biome.values().toString());
        Utils.cm("COORDINATES: X: " + chunk.getX() + " Z: " + chunk.getZ());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final Block block = chunk.getBlock(x, 0, z);
                block.setBiome(biome);
            }
        }

        if (!pPlayer.hasPermission("isoworlds.unlimited.charges")) {
            Utils.updateCharge(pPlayer, charges - 1, Msg.keys.SQL);
        }
        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.RED + "Vous venez d'utiliser une charge, nouveau compte: " + ChatColor.GREEN + (charges - 1) + " charge(s)");

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vient de changer le biome du chunk dans lequel vous êtes. (F9)");

        instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.BIOME, Cooldown.BIOME_DELAY);
    }
}
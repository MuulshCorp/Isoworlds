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
package bukkit.util;

import bukkit.MainBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

import java.util.Arrays;

public class DimsAlt {

    private static final MainBukkit plugin = MainBukkit.instance;

    public static void generateDim() {
        // Remove files
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

            @Override
            public void run() {

                String[] dimsSkyblock = new String[]{"MS3", "SF3", "AS2", "PO2", "PO2K"};
                String[] dims;

                // Si contient alors on met pas le minage
                if (!Arrays.asList(dimsSkyblock).contains(plugin.servername)) {

                    dims = new String[]{"exploration", "minage"};

                    for (String dim : dims) {

                        // Create
                        Bukkit.getServer().createWorld(new WorldCreator(dim));

                        // Load world
                        Bukkit.getServer().createWorld(new WorldCreator(dim));

                        // Set properties
                        setWorldProperties(dim);
                    }
                }
            }
        }, 60 * 20);
    }

    private static void setWorldProperties(String worldname) {

        // Properties of IsoWorld
        World world = Bukkit.getServer().getWorld(worldname);

        Logger.severe("Size: " + 3000 + " " + 3000);
        Utils.cmd("wb " + worldname + " set " + 3000 + " " + 3000 + " 0 0");

        if (world != null) {
            Block yLoc = world.getHighestBlockAt(0, 0);
            world.setPVP(false);
            world.setSpawnLocation(0, yLoc.getY(), 0);
            world.setGameRuleValue("MobGriefing", "false");
            world.setAutoSave(true);
        }
        Utils.cm("WorldProperties à jour");
    }
}

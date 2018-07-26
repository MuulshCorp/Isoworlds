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
package sponge.location;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.event.cause.Cause;
import sponge.Main;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.util.console.Logger;

import java.util.Optional;

public class Locations {

    private static final Main plugin = Main.instance;

    public static Optional<Location<World>> getHighestLoc(Location<World> loc) {
        Optional<Integer> y = getHighestY(loc.getExtent(), loc.getX(), loc.getZ(), loc.getBlockY());
        return y.map(integer -> new Location<>(loc.getExtent(), loc.getX(), integer + 1, loc.getZ()));
    }

    private static boolean isPassable(World w, Double x, int y, Double z) {
        Optional<MatterProperty> prop = new Location<>(w, x, y, z).getBlock().getProperty(MatterProperty.class);
        return prop.get().getValue().toString().equals("GAS");
    }

    private static Optional<Integer> getHighestY(World w, Double x, Double z, int baseY) {
        // If y 0 then auto, else we start from defined value
        int y = baseY;
        if (baseY == 0) {
            y = w.getBlockMax().getY();
        }
        while (isPassable(w, x, y, z)) {
            y = y - 1;
            if (y <= 0) {
                return Optional.empty();
            }
        }
        return Optional.of(y);
    }

    public static boolean teleport(Player player, String worldname) {

        Location<World> maxy;
        Optional<World> finalWorld = plugin.getGame().getServer().getWorld(getOfficialDimSpawn(worldname));

        if (finalWorld.isPresent()) {

            try {

                Location<World> spawn = finalWorld.get().getSpawnLocation();
                // Actual spawn location

                // Set to 61 for official dimensions
                Location<World> destination = new Location<>(spawn.getExtent(), getAxis(worldname).getX(), 61, getAxis(worldname).getZ());

                // If dimensions if not autobuilt, return the same name so it can build Isoworlds safe zone
                if (getOfficialDimSpawn(worldname).equals(worldname)) {

                    // Get max location, if Y axis is 0 then it will find from max, else find from the one set to lower
                    maxy = Locations.getHighestLoc(new Location<>(spawn.getExtent(), getAxis(worldname).getX(), getAxis(worldname).getY(), getAxis(worldname).getZ()))
                            .orElse(new Location<>(spawn.getExtent(), getAxis(worldname).getX(), 61, getAxis(worldname).getZ()));

                    destination = new Location<>(spawn.getExtent(), getAxis(worldname).getX(), maxy.getBlockY(), getAxis(worldname).getZ());

                    // Set dirt if liquid or air
                    if (!finalWorld.get().getBlock(getAxis(worldname).getFloorX(), destination.getBlockY() - 1, getAxis(worldname).getFloorZ())
                            .getProperty(MatterProperty.class).get().getValue().toString().equals("SOLID")) {
                        // Build safe zone
                        finalWorld.get().getLocation(destination.getBlockX(), destination.getBlockY() - 1, destination.getBlockZ()).setBlockType(BlockTypes.DIRT, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
                    }
                }

                // Téléportation du joueur
                if (player.setLocationSafely(destination)) {
                    Logger.info("Le joueur a bien été téléporté !");
                } else {
                    Logger.info("Le joueur n'a pas pu être téléporté !");
                    return false;
                }

            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

        }
        return true;
    }

    private static void buildSafeSpawn(String worldname, String casualName) {

        Sponge.getServer().loadWorld(worldname);

        // Clear zone
        for (int x = -2; x < 2; x++) {
            for (int y = 60; y < 65; y++) {
                for (int z = -2; z < 2; z++) {
                    if (Sponge.getServer().getWorld(worldname).get().getBlock(x, y, x).getType() != BlockTypes.BEDROCK) {
                        Sponge.getServer().getWorld(worldname).get().setBlockType(x, y, z, BlockTypes.AIR, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
                    }
                }
            }
        }

        // Build safe zone
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Sponge.getServer().getWorld(worldname).get().setBlockType(x, 60, z, BlockTypes.BEDROCK, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());
            }
        }

        // Set sign
        //Sponge.getServer().getWorld(worldname).get().setBlockType(-2, 61, -2, BlockTypes.TORCH, Cause.source(Sponge.getPluginManager().fromInstance(plugin).get()).build());

    }

    public static Vector3d getAxis(String worldname) {
        return (new Vector3d(0.500, 0.0, 0.500));
    }

    // Get name, null if not official
    private static String getOfficialDimSpawn(String worldname) {

        // Define dimension name
        if (worldname.equals("end")) {
            worldname = "DIM1";
            buildSafeSpawn(worldname, "END");
            return worldname;
        } else if (worldname.equals("nether")) {
            // Teleport to nether
            worldname = "DIM-1";
            buildSafeSpawn(worldname, "NETHER");
            return worldname;
        }
        return worldname;
    }
}

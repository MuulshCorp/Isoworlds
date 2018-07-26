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
package sponge.util.action;

import common.ManageFiles;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.Main;
import sponge.util.console.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DimsAltAction {

    private static final Main plugin = Main.instance;

    private static final DataQuery toId = DataQuery.of("SpongeData", "dimensionId");

    public static void generateDim() {

        String[] dimsSkyblock = new String[]{"MS3", "SF3", "AS2", "PO2", "PO2K"};
        String[] dims;

        // Si contient alors on met pas le minage
        if (!Arrays.asList(dimsSkyblock).contains(plugin.servername)) {

            dims = new String[]{"exploration", "minage"};

            for (String dim : dims) {
                // Path dim

                // Set properties
                setWorldProperties(dim);

                // Set id
                setId(dim);

                // Load world
                Sponge.getGame().getServer().loadWorld(dim);
            }
        }
    }

    private static void setWorldProperties(String worldname) {
        // Create world properties Isoworlds

        // Check si world properties en place, création else
        Optional<WorldProperties> wp = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties worldProperties;

        try {
            if (wp.isPresent()) {
                worldProperties = wp.get();
                Logger.info("WOLRD PROPERTIES: déjà présent");
                worldProperties.setKeepSpawnLoaded(true);
                worldProperties.setLoadOnStartup(true);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setPVPEnabled(false);
                worldProperties.setWorldBorderCenter(0, 0);
                worldProperties.setWorldBorderDiameter(6000);
                worldProperties.setEnabled(true);
                Sponge.getServer().saveWorldProperties(worldProperties);
                // Border
                Optional<World> world = Sponge.getServer().getWorld(worldname);
                if (world.isPresent()) {
                    world.get().getWorldBorder().setDiameter(6000);
                }
                Logger.warning("Border nouveau: " + 6000);
            } else {
                worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
                Logger.info("WOLRD PROPERTIES: non présents, création...");
                worldProperties.setKeepSpawnLoaded(true);
                worldProperties.setLoadOnStartup(true);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setPVPEnabled(false);
                worldProperties.setWorldBorderCenter(0, 0);
                worldProperties.setWorldBorderDiameter(6000);
                Sponge.getServer().saveWorldProperties(worldProperties);
                Logger.warning("Border nouveau: " + 6000);
            }
            Logger.info("WorldProperties à jour");

        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private static void setId(String dim) {
        // TEST
        Path levelSponge = Paths.get(ManageFiles.getPath() + dim + "/level_sponge.dat");
        if (Files.exists(levelSponge)) {
            DataContainer dc;
            boolean gz = false;
            int dimId;

            // Find dat
            try (GZIPInputStream gzip = new GZIPInputStream(Files.newInputStream(levelSponge, StandardOpenOption.READ))) {
                dc = DataFormats.NBT.readFrom(gzip);
                gz = true;

                if (dim.equals("minage")) {
                    dimId = 99998;
                } else if (dim.equals("exploration")) {
                    dimId = 99999;
                } else {
                    return;
                }

                dc.set(toId, dimId);

                // define dat
                try (OutputStream os = getOutput(gz, levelSponge)) {
                    DataFormats.NBT.writeTo(os, dc);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static OutputStream getOutput(boolean gzip, Path file) throws IOException {
        OutputStream os = Files.newOutputStream(file);
        if (gzip) {
            return new GZIPOutputStream(os, true);
        }

        return os;
    }
}

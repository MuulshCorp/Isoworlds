package sponge.Utils;

import common.ManageFiles;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Edwin on 01/05/2018.
 */
public class IsoWorldsDimensionsALT {

    private static final DataQuery toId = DataQuery.of("SpongeData", "dimensionId");

    public static void generateDim() {

        String[] dims = new String[]{"exploration", "minage"};


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

    private static void setWorldProperties(String worldname) {
        // Create world properties IsoWorlds

        // Check si world properties en place, création else
        Optional<WorldProperties> wp = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties worldProperties;

        try {
            if (wp.isPresent()) {
                worldProperties = wp.get();
                IsoworldsUtils.cm("WOLRD PROPERTIES: déjà présent");
                worldProperties.setKeepSpawnLoaded(true);
                worldProperties.setLoadOnStartup(true);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
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
                IsoworldsLogger.warning("Border nouveau: " + 6000);
            } else {
                worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
                IsoworldsUtils.cm("WOLRD PROPERTIES: non présents, création...");
                worldProperties.setKeepSpawnLoaded(true);
                worldProperties.setLoadOnStartup(true);
                worldProperties.setGenerateSpawnOnLoad(false);
                worldProperties.setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
                worldProperties.setPVPEnabled(false);
                worldProperties.setWorldBorderCenter(0, 0);
                worldProperties.setWorldBorderDiameter(6000);
                Sponge.getServer().saveWorldProperties(worldProperties);
                IsoworldsLogger.warning("Border nouveau: " + 6000);
            }
            IsoworldsUtils.cm("WorldProperties à jour");

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

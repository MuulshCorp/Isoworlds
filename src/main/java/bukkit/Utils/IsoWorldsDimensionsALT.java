package bukkit.Utils;

import bukkit.IsoworldsBukkit;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;

import java.util.Arrays;

/**
 * Created by Edwin on 01/05/2018.
 */
public class IsoWorldsDimensionsALT {

    private static final IsoworldsBukkit plugin = IsoworldsBukkit.instance;

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


        IsoworldsLogger.severe("Size: " + 3000 + " " + 3000);
        IsoworldsUtils.cmd("wb " + worldname + " set " + 3000 + " " + 3000 + " 0 0");

        if (world != null) {
            Block yLoc = world.getHighestBlockAt(0, 0);
            world.setPVP(false);
            world.setSpawnLocation(0, yLoc.getY(), 0);
            world.setGameRuleValue("MobGriefing", "false");
            world.setAutoSave(true);
        }

        IsoworldsUtils.cm("WorldProperties à jour");

    }
}

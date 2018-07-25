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
package bukkit.configuration;

import bukkit.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {

    private static final FileConfiguration configurationNode = Main.getInstance().getConfig();

    public static String getId() {
        return (configurationNode.getString("Id"));
    }

    public static String getMainWorld() {
        return (configurationNode.getString("MainWorld"));
    }

    public static String getMainWorldSpawnCoordinate() {
        return (configurationNode.getString("MainWorldSpawnCoordinate"));
    }

    public static boolean getAutomaticUnload() {
        return (configurationNode.getBoolean("Modules.AutomaticUnload.Enabled"));
    }

    public static Integer getInactivityTime() {
        return (configurationNode.getInt("Modules.AutomaticUnload.InactivityTime"));
    }

    public static boolean getStorage() {
        return (configurationNode.getBoolean("Modules.Storage.Enabled"));
    }

    public static boolean getDimensionAlt() {
        return (configurationNode.getBoolean("Modules.DimensionAlt.Enabled"));
    }

    public static boolean getMining() {
        return (configurationNode.getBoolean("Modules.DimensionAlt.Mining"));
    }

    public static boolean getExploration() {
        return (configurationNode.getBoolean("Modules.DimensionAlt.Exploration"));
    }

    public static boolean getSafePlateform() {
        return (configurationNode.getBoolean("Modules.SafePlateform.Enabled"));
    }

    public static boolean getSafeSpawn() {
        return (configurationNode.getBoolean("Modules.SafeSpawn.Enabled"));
    }

    public static boolean getSpawnProtection() {
        return (configurationNode.getBoolean("Modules.SpawnProtection.Enabled"));
    }

    public static boolean getBorder() {
        return (configurationNode.getBoolean("Modules.Border.Enabled"));
    }

    public static Integer getDefaultRadiusSize() {
        return (configurationNode.getInt("Modules.Border.DefaultRadiusSize"));
    }

    public static Integer getSmallRadiusSize() {
        return (configurationNode.getInt("Modules.Border.SmallRadiusSize"));
    }

    public static Integer getMediumRadiusSize() {
        return (configurationNode.getInt("Modules.Border.MediumRadiusSize"));
    }

    public static Integer getLargeRadiusSize() {
        return (configurationNode.getInt("Modules.Border.LargeRadiusSize"));
    }

    public static boolean getPlayTime() {
        return (configurationNode.getBoolean("Modules.PlayTime.Enabled"));
    }
}

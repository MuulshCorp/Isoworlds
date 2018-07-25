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
package sponge.configuration;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import sponge.Main;

public class Configuration {

    private static final CommentedConfigurationNode configurationNode = Main.instance.getConfig();

    public static String getId() {
        return ((String) configurationNode.getNode(new Object[]{"IsoWorlds", "Id"}).getValue());
    }

    public static String getMainWorld() {
        return ((String) configurationNode.getNode(new Object[]{"IsoWorlds", "MainWorld"}).getValue());
    }

    public static String getMainWorldSpawnCoordinate() {
        return ((String) configurationNode.getNode(new Object[]{"IsoWorlds", "MainWorldSpawnCoordinate"}).getValue());
    }

    public static boolean getAutomaticUnload() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "AutomaticUnload", "Enabled"}).getValue());
    }

    public static Integer getInactivityTime() {
        return ((Integer) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "AutomaticUnload", "InactivityTime"}).getValue());
    }

    public static boolean getStorage() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "Storage", "Enabled"}).getValue());
    }

    public static boolean getDimensionAlt() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "DimensionAlt", "Enabled"}).getValue());
    }

    public static boolean getMining() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "DimensionAlt", "Mining"}).getValue());
    }

    public static boolean getExploration() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "DimensionAlt", "Exploration"}).getValue());
    }

    public static boolean getSafePlateform() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "SafePlateform", "Enabled"}).getValue());
    }

    public static boolean getSafeSpawn() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "SafeSpawn", "Enabled"}).getValue());
    }

    public static boolean getSpawnProtection() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "SpawnProtection", "Enabled"}).getValue());
    }

    public static boolean getBorder() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "Border", "Enabled"}).getValue());
    }

    public static Integer getDefaultRadiusSize() {
        return ((Integer) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "Border", "DefaultRadiusSize"}).getValue());
    }

    public static Integer getSmallRadiusSize() {
        return ((Integer) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "Border", "SmallRadiusSize"}).getValue());
    }

    public static Integer getMediumRadiusSize() {
        return ((Integer) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "Border", "MediumRadiusSize"}).getValue());
    }

    public static Integer getLargeRadiusSize() {
        return ((Integer) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "Border", "LargeRadiusSize"}).getValue());
    }

    public static boolean getPlayTime() {
        return ((boolean) configurationNode.getNode(new Object[]{"IsoWorlds", "Modules", "PlayTime", "Enabled"}).getValue());
    }

}

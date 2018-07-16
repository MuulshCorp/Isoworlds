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
package bukkit.util.console;

import bukkit.MainBukkit;
import org.bukkit.ChatColor;

public class Logger {
    private static final MainBukkit plugin = MainBukkit.instance;

    public static void info(String s) {
        plugin.getServer().getConsoleSender().sendMessage("[" + ChatColor.GOLD + "IW" + ChatColor.WHITE + "] " + ChatColor.GREEN + s);
    }

    public static void warning(String s) {
        plugin.getServer().getConsoleSender().sendMessage("[" + ChatColor.GOLD + "IW" + ChatColor.WHITE + "] " + ChatColor.GOLD + s);
    }

    public static void severe(String s) {
        plugin.getServer().getConsoleSender().sendMessage("[" + ChatColor.GOLD + "IW" + ChatColor.WHITE + "] " + ChatColor.RED + s);
    }

    public static void tracking(String s) {
        plugin.getServer().getConsoleSender().sendMessage("[" + ChatColor.GOLD + "TRACKING-IW" + ChatColor.WHITE + "] " + ChatColor.AQUA + s);
    }

    public static void tag() {
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + " _____          __          __           _      _      ");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "|_   _|         \\ \\        / /          | |    | |     ");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "  | |   ___   ___\\ \\  /\\  / /___   _ __ | |  __| | ___ ");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "  | |  / __| / _ \\\\ \\/  \\/ // _ \\ | '__|| | / _` |/ __|");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + " _| |_ \\__ \\| (_) |\\  /\\  /| (_) || |   | || (_| |\\__ \\");
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "|_____||___/ \\___/  \\/  \\/  \\___/ |_|   |_| \\__,_||___/");
    }

}

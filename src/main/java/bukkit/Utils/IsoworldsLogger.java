package bukkit.Utils;

import bukkit.IsoworldsBukkit;
import org.bukkit.ChatColor;

/**
 * Created by Edwin on 26/01/2018.
 */
public class IsoworldsLogger {
    private static final IsoworldsBukkit plugin = IsoworldsBukkit.instance;

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

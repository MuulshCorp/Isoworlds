package bukkit.Utils;

import org.bukkit.Bukkit;

/**
 * Created by Edwin on 20/10/2017.
 */
public class IworldsUtils {

    // Console message
    public static void cm(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

    // Commande console
    public static void cmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }



}

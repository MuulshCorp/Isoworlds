package bukkit.util;

import bukkit.MainBukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Edwin on 16/07/2018.
 */
public class Lock {

    private static final MainBukkit instance = MainBukkit.getInstance();

    // Vérifie si le lock est présent et renvoi vrai, sinon défini le lock et renvoi false
    public static Boolean isLocked(Player pPlayer, String className) {
        // Si le lock est set, alors on renvoie false avec un message de sorte à stopper la commande et informer le jouer
        if (checkLockFormat(pPlayer.getUniqueId().toString(), String.class.getName())) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania indique que vous devez patienter avant de pouvoir utiliser de nouveau cette commande.");
            return true;
        } else {
            // On set lock
            instance.lock.put(pPlayer.getUniqueId().toString() + ";" + className, 1);
            return false;
        }
    }

    // Cooldown modèle: uuid;commande
    public static Boolean checkLockFormat(String pPlayer, String command) {
        // Si le tableau est null alors lock 0 sinon lock 1
        if (instance.lock.get(pPlayer + ";" + command) == null) {
            return false;
        } else {
            return true;
        }
    }
}

package bukkit.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * Created by Edwin on 20/11/2017.
 */

public class IsoWorldsTasks extends BukkitRunnable {
    private int check = 20;
    private Player pPlayer;
    private File file;

    public IsoWorldsTasks(Player pPlayer, File file) {
        this.pPlayer = pPlayer;
        this.file = file;
    }

    @Override
    public void run() {
        if (check == 20) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania est sur le point de ramener votre IsoWorld dans ce royaume, veuillez patienter... (Temps estimé: 20 secondes)");
        }
        check --;
        if (check < 1) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania ne parvient pas à charger votre monde, veuillez re tenter ou contacter l'équipe Isolonice.");
            cancel();
        } else if (file.exists()) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + "Sijania vient de terminer son travail, l'IsoWorld est disponible !");
            pPlayer.performCommand("iw h");
            cancel();
        }
    }
}
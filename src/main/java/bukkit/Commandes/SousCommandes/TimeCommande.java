package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;

import common.Cooldown;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import bukkit.Utils.IsoworldsUtils;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 24/10/2017.
 */
public class TimeCommande {

    public static IsoworldsBukkit instance;

    public static void Time(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();
        int num;
        Player pPlayer = (Player) sender;
        Integer len = args.length;

        //If the method return true then the command is in lock
        if (!instance.cooldown.isAvailable(pPlayer, Cooldown.TIME)) {
            return;
        }

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return;
        }

        if (!IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]" + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        if (len < 2) {
            pPlayer.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "IsoWorlds " + ChatColor.GOLD + "] ---------------------");
            pPlayer.sendMessage(" ");
            pPlayer.sendMessage(ChatColor.AQUA + "Sijania vous propose deux temps:");
            pPlayer.sendMessage(ChatColor.GOLD + "- Jour: " + ChatColor.AQUA + "/iw time " + ChatColor.GOLD + "[" + ChatColor.GREEN + "jour"
                    + ChatColor.GOLD + "/" + ChatColor.GREEN + "nuit" + ChatColor.GOLD + "]");
            pPlayer.sendMessage(" ");
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        } else {
            try{
                num = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania indique que vous n'avez pas renseigné de minutes.");
                instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            }
            World weather = Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld");
            IsoworldsUtils.cm("Time world: " + weather.getName());
            if (args[1].equals("jour") || args[1].equals("day")) {
                weather.setTime(0);
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania vient de changer le temps de votre IsoWorld.");
                instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            } else if (args[1].equals("nuit") || args[1].equals("night")) {
                weather.setTime(12000);
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania vient de changer la météo de votre IsoWorld.");
                instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            }
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.TIME, Cooldown.TIME_DELAY);
        }
    }
}

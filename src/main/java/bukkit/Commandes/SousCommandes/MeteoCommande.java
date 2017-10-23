package bukkit.Commandes.SousCommandes;

import bukkit.IworldsBukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import bukkit.Utils.IworldsUtils;

/**
 * Created by Edwin on 24/10/2017.
 */
public class MeteoCommande {

    static final String SELECT = "SELECT * FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";

    public static IworldsBukkit instance;

    public static void Meteo(CommandSender sender, String[] args) {

        instance = IworldsBukkit.getInstance();

        int num;

        Player pPlayer = (Player) sender;
        Integer len = args.length;

        if (IworldsUtils.iworldExists(pPlayer, "Sijania vient de changer le temps de votre iWorld.") == false) {
            pPlayer.sendMessage(ChatColor.GOLD + "[iWorlds]" + ChatColor.AQUA + "Sijania indique que vous ne possédez aucun iWorld.");
            return;
        }

        if (len < 3) {
            pPlayer.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "iWorlds " + ChatColor.GOLD + "] ---------------------");
            pPlayer.sendMessage(" ");
            pPlayer.sendMessage(ChatColor.AQUA + "Sijania vous propose trois types de météo:");
            pPlayer.sendMessage(ChatColor.GOLD + "- Pluie: " + ChatColor.AQUA + "/iw meteo " + ChatColor.GOLD + "[" + ChatColor.GREEN + "pluie"
                    + ChatColor.GOLD + "/" + ChatColor.GREEN + "soleil" + ChatColor.GOLD + "] " + ChatColor.GREEN + "(durée en minute)");
            pPlayer.sendMessage(" ");
            return;
        } else {
            try{
                num = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania indique que vous devez n'avez pas renseigné de minutes.");
                return;
            }
            World weather = Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-iWorld");
            IworldsUtils.cm("Weather world: " + weather.getName());
            if (args[1].equals("pluie") || args[1].equals("rain")) {
                weather.setStorm(true);
                weather.setWeatherDuration(num);
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania vient de changer la météo de votre iWorld.");
                return;
            } else if (args[1].equals("soleil") || args[1].equals("sun")) {
                weather.setStorm(false);
                weather.setWeatherDuration(num);
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania vient de changer la météo de votre iWorld.");
                return;
            }
            return;
        }
    }
}

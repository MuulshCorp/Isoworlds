package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;

import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import bukkit.Utils.IsoworldsUtils;

import static bukkit.Utils.IsoworldsUtils.isSetCooldown;

/**
 * Created by Edwin on 24/10/2017.
 */
public class MeteoCommande {

    public static IsoworldsBukkit instance;

    public static void Meteo(CommandSender sender, String[] args) {

        instance = IsoworldsBukkit.getInstance();
        int num;
        Player pPlayer = (Player) sender;
        Integer len = args.length;

        // Si la méthode renvoi vrai alors on return car le cooldown est défini, sinon elle le set auto
        if (isSetCooldown(pPlayer, String.class.getName())) {
            return;
        }

        if (!IsoworldsUtils.iworldExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]" + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        if (len < 3) {
            pPlayer.sendMessage(ChatColor.GOLD + "--------------------- [ " + ChatColor.AQUA + "IsoWorlds " + ChatColor.GOLD + "] ---------------------");
            pPlayer.sendMessage(" ");
            pPlayer.sendMessage(ChatColor.AQUA + "Sijania vous propose trois types de météo:");
            pPlayer.sendMessage(ChatColor.GOLD + "- Pluie: " + ChatColor.AQUA + "/iw meteo " + ChatColor.GOLD + "[" + ChatColor.GREEN + "pluie"
                    + ChatColor.GOLD + "/" + ChatColor.GREEN + "soleil" + ChatColor.GOLD + "] " + ChatColor.GREEN + "(durée en minute)");
            pPlayer.sendMessage(" ");
            instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        } else {
            try {
                num = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                pPlayer.sendMessage(ChatColor.AQUA + "Sijania indique que vous n'avez pas renseigné de minutes.");
                instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            }
            World weather = Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld");
            IsoworldsUtils.cm("Weather world: " + weather.getName());
            if (args[1].equals("pluie") || args[1].equals("rain")) {
                weather.setStorm(true);
                weather.setWeatherDuration(num);
            } else if (args[1].equals("soleil") || args[1].equals("sun")) {
                weather.setStorm(false);
                weather.setWeatherDuration(num);
                // Message pour tous les joueurs
            }
            for (Player p : Bukkit.getServer().getWorld(pPlayer.getUniqueId().toString() + "-IsoWorld").getPlayers()) {
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds] Sijania indique que " + pPlayer.getName()
                        + " vient de changer la météo à: " + args[1] + " pendant: " + num + " ticks.");
            }
        }
        instance.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return;
    }
}

package bukkit.Commandes.SousCommandes;

import bukkit.IsoworldsBukkit;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.ManageFiles;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static bukkit.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 20/10/2017.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class RefonteCommande {

    private final static Map<String, Timestamp> confirm = new HashMap<>();
    public static IsoworldsBukkit instance;

    public static void Refonte(CommandSender sender, String[] args) {
        // Variables
        String fullpath = "";
        String worldname;
        Player pPlayer = (Player) sender;
        instance = IsoworldsBukkit.getInstance();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        //If the method return true then the command is in lock
        Timestamp cooldown = instance.cooldown.getPlayerLastCooldown(pPlayer, Cooldown.REFONTE);
        if (cooldown != null) {
            String timerMessage = instance.cooldown.getCooldownTimer(cooldown);
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.UNAVAILABLE_COMMAND + timerMessage);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return;
        }

        // SELECT WORLD
        if (!IsoworldsUtils.iworldExists(pPlayer.getUniqueId().toString(), Msg.keys.SQL)) {
            IsoworldsUtils.cm("DEBUG 1");
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        // Confirmation
        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if (minutes >= 1) {
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.CONFIRMATION);
                instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return;
            }
        }

        confirm.remove(pPlayer.getUniqueId().toString());

        fullpath = (ManageFiles.getPath() + pPlayer.getUniqueId().toString() + "-IsoWorld");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        File sourceDir = new File(ManageFiles.getPath() + worldname);
        File destDir = new File(ManageFiles.getPath() + "/IsoWorlds-REFONTE/" + worldname);
        destDir.mkdir();

        if (Bukkit.getServer().getWorld(worldname) == null) {
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_PAS_IWORLD);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        if (Bukkit.getServer().getWorld(worldname) != null) {
            Collection<Player> colPlayers = Bukkit.getServer().getWorld(worldname).getPlayers();
            Integer maxY = Bukkit.getServer().getWorld(worldname).getHighestBlockYAt(0, 0);
            Location overworld = new Location(Bukkit.getServer().getWorld("Isolonice"), 0, maxY, 0);
            for (Player player : colPlayers) {
                player.teleport(overworld);
                pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.REFONTE_KICK);
            }
            World world = Bukkit.getServer().getWorld(worldname);
            Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(worldname), true);
        }

        //iWorldsUtils.deleteDir(sourceDir);
        File remove = new File((ManageFiles.getPath() + worldname));
        ManageFiles.deleteDir(remove);

        // DELETE WORLD
        if (!IsoworldsUtils.deleteIworld(pPlayer, Msg.keys.SQL)) {
            IsoworldsUtils.cm("DEBUG 2");
            pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.EXISTE_IWORLD);
            instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return;
        }

        pPlayer.sendMessage(ChatColor.GOLD + "[IsoWorlds]: " + ChatColor.AQUA + Msg.keys.KICK_TRUST);
        CommandSender newSender = pPlayer.getPlayer();
        //Start the lock for this command
        if (!instance.cooldown.addPlayerCooldown(pPlayer, Cooldown.REFONTE, Cooldown.REFONTE_DELAY)) {
            IsoworldsUtils.cm(Msg.keys.SQL);
        }
        instance.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        CreationCommande.Creation(newSender, args);
    }
}

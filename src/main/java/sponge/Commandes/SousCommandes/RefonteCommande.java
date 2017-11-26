package sponge.Commandes.SousCommandes;

import common.Cooldown;
import common.ManageFiles;
import common.Msg;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.*;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.File;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static sponge.Utils.IsoworldsUtils.isLocked;

/**
 * Created by Edwin on 10/10/2017.
 */
public class RefonteCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;
    final static Map<String, Timestamp> confirm = new HashMap<String, Timestamp>();

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;

        //If the method return true then the command is in lock
        Timestamp cooldown = Cooldown.getPlayerLastCooldown(pPlayer, Cooldown.REFONTE);
        if (cooldown != null) {
            String timerMessage = Cooldown.getCooldownTimer(cooldown);
            pPlayer.sendMessage(Text.of(Text.builder(Msg.keys.BASE_MESSAGE + Msg.keys.UNAVAILABLE_COMMAND + timerMessage)));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        // SELECT WORLD
        if (!IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        // Confirmation
        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {;
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.CONFIRMATION).color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if (minutes >= 1) {
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.CONFIRMATION).color(TextColors.AQUA))).build()));
                plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                return CommandResult.success();
            }
        }
        confirm.remove(pPlayer.getUniqueId().toString());
        fullpath = (ManageFiles.getPath() + IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        File sourceDir = new File(ManageFiles.getPath() + worldname);
        File destDir = new File(ManageFiles.getPath() + "/IsoWorlds-REFONTE/" + worldname);
        destDir.mkdir();
        if (!Sponge.getServer().getWorld(worldname).isPresent()) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }
        if (Sponge.getServer().getWorld(worldname).get().isLoaded()) {
            Collection<Player> colPlayers = Sponge.getServer().getWorld(worldname).get().getPlayers();
            Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
            for (Player player : colPlayers) {
                player.setLocation(spawn);
                player.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.REFONTE_KICK).color(TextColors.AQUA))).build()));
            }
            World world = Sponge.getServer().getWorld(worldname).get();
            Sponge.getServer().unloadWorld(world);
        }

        //iWorldsUtils.deleteDir(sourceDir);
        Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties world = optionalWorld.get();
        try {
            if (Sponge.getServer().deleteWorld(world).get()) {
                IsoworldsUtils.cm("Le monde: " + worldname + " a bien été supprimé");
            }
        } catch (InterruptedException | ExecutionException ie) {
            ie.printStackTrace();
        }


        // DELETE WORLD
        if (!IsoworldsUtils.deleteIworld(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_IWORLD).color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(Msg.keys.SUCCES_REFONTE).color(TextColors.AQUA))).build()));
        Sponge.getCommandManager().process(pPlayer, "iw creation");
        plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return CommandResult.success();
    }


    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commandes de refonte des iWorlds"))
                    .permission("iworlds.refonte")
                .executor(new RefonteCommande())
                .build();
    }
}

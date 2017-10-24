package sponge.Commandes.SousCommandes;

import common.ManageFiles;
import common.Msg;
import javafx.util.converter.TimeStringConverter;
import sponge.IworldsSponge;
import sponge.Utils.IworldsUtils;

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
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edwin on 10/10/2017.
 */
public class RefonteCommande implements CommandExecutor {

    private final IworldsSponge plugin = IworldsSponge.instance;
    final static Map<String, Timestamp> confirm = new HashMap<String, Timestamp>();

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        final String Iuuid_p;
        final String Iuuid_w;
        final String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_W` = ?";
        final String DELETE_IWORLDS = "DELETE FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;

        if (!(confirm.containsKey(pPlayer.getUniqueId().toString()))) {;
            confirm.put(pPlayer.getUniqueId().toString(), timestamp);
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.CONFIRMATION).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        } else {
            long millis = timestamp.getTime() - (confirm.get(pPlayer.getUniqueId().toString()).getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
            if (minutes >= 1) {
                confirm.remove(pPlayer.getUniqueId().toString());
                pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.CONFIRMATION).color(TextColors.AQUA))).build()));
                return CommandResult.success();
            }
        }
        confirm.remove(pPlayer.getUniqueId().toString());
        fullpath = (ManageFiles.getPath() + IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        File sourceDir = new File(ManageFiles.getPath() + worldname);
        File destDir = new File(ManageFiles.getPath() + "/iWORLDS-REFONTE/" + worldname);
        destDir.mkdir();
        if (!Sponge.getServer().getWorld(worldname).isPresent()) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_PAS_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }
        if (Sponge.getServer().getWorld(worldname).get().isLoaded()) {
            Collection<Player> colPlayers = Sponge.getServer().getWorld(worldname).get().getPlayers();
            Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
            for (Player player : colPlayers) {
                player.setLocation(spawn);
                player.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
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
                IworldsUtils.cm("Le monde: " + worldname + " a bien été supprimé");
            }
        } catch (InterruptedException | ExecutionException ie) {
            ie.printStackTrace();
        }

        // DELETE
        try {
            PreparedStatement delete_autorisations = plugin.database.prepare(DELETE_AUTORISATIONS);
            PreparedStatement delete_iworlds = plugin.database.prepare(DELETE_IWORLDS);
            // UUID_P
            Iuuid_p = pPlayer.getUniqueId().toString();
            delete_iworlds.setString(1, Iuuid_p);
            // UUID_W
            Iuuid_w = (pPlayer.getUniqueId().toString() + "-iWorld");
            delete_autorisations.setString(1, Iuuid_w);
            delete_iworlds.setString(2, Iuuid_w);
            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.SQL).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(Msg.keys.SUCCES_REFONTE).color(TextColors.AQUA))).build()));
        Sponge.getCommandManager().process(pPlayer, "iw creation");
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

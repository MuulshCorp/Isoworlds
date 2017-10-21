package isolonice.iworlds.Commandes.SousCommandes;

import commonUtils.ManageFiles;
import isolonice.iworlds.IworldsSponge;
import isolonice.iworlds.Utils.IworldsUtils;

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
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Created by Edwin on 10/10/2017.
 */
public class RefonteCommande implements CommandExecutor {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        final String Iuuid_p;
        final String Iuuid_w;
        final String DELETE_AUTORISATIONS = "DELETE FROM `autorisations` WHERE `UUID_W` = ?";
        final String DELETE_IWORLDS = "DELETE FROM `iworlds` WHERE `UUID_P` = ? AND `UUID_W` = ?";

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;

        fullpath = (ManageFiles.getPath() + IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        File sourceDir = new File(ManageFiles.getPath() + worldname);
        File destDir = new File(ManageFiles.getPath() + "/iWORLDS-REFONTE/" + worldname);
        destDir.mkdir();
        if (!Sponge.getServer().getWorld(worldname).isPresent()) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que vous ne possédez aucun iWorld, elle vous recommande d'entrez la commande: /iw creation.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }
        if (Sponge.getServer().getWorld(worldname).get().isLoaded()) {
            Collection<Player> colPlayers = Sponge.getServer().getWorld(worldname).get().getPlayers();
            Location<World> spawn = Sponge.getServer().getWorld("Isolonice").get().getSpawnLocation();
            for (Player player : colPlayers) {
                player.setLocation(spawn);
                player.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("Sijania entame une destruction entière de l'iWorld dans lequel vous vous trouviez sur demande de son propriétaire, vous avez été renvoyé au spawn pour votre protection.").color(TextColors.AQUA))).build()));
            }
            World world = Sponge.getServer().getWorld(worldname).get();
            Sponge.getServer().unloadWorld(world);
            IworldsUtils.cm("L'iWorld :" + worldname + " a bien été déchargé");
        }



        //iWorldsUtils.deleteDir(sourceDir);
        IworldsUtils.cm("Le monde :" + worldname + " a bien été déplacé dans le dossier de refonte");
        Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(worldname);
        WorldProperties world = optionalWorld.get();
        try {
            if (Sponge.getServer().deleteWorld(world).get()) {
                IworldsUtils.cm("Le monde: " + worldname + " a bien été supprimé");
                IworldsUtils.cm("Le properties du monde ont bien été supprimées");
            }
        } catch (InterruptedException | ExecutionException ie) {
            ie.printStackTrace();
        }

        //move(sourceDir, destDir);
        IworldsUtils.cm("sourceDir: " + sourceDir);
        IworldsUtils.cm("destDir: " + destDir);

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

            IworldsUtils.cm("DELETE_AUTORISATIONS: " + delete_autorisations);
            IworldsUtils.cm("DELETE_IWORLDS" + delete_iworlds);

            delete_autorisations.executeUpdate();
            delete_iworlds.executeUpdate();
        } catch (Exception ex) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("INSERT Sijania n'est pas parvenue à refondre votre iWorld, veuillez contacter l'équipe Isolonice.").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        IworldsUtils.cm("Fin de la procédure de refonte");
        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder("Sijania vient de terminer son travail, vous pouvez lui demander un nouveau iWorld en entrant la commande: /iw creation.").color(TextColors.AQUA))).build()));
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

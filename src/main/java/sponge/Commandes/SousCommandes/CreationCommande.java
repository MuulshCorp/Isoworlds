package sponge.Commandes.SousCommandes;

import common.Msg;
import sponge.IworldsSponge;
import sponge.Locations.IworldsLocations;
import sponge.Utils.IworldsUtils;
import common.ManageFiles;

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

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import static sponge.Utils.IworldsUtils.iworldExists;

/**
 * Created by Edwin on 05/10/2017.
 */

public class CreationCommande implements CommandExecutor {
    static final String INSERT = "INSERT INTO `iworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    static final String INSERT_TRUST = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;
        IworldsUtils.iworldExists(pPlayer, Msg.keys.SQL);
        IworldsUtils.coloredMessage(pPlayer, Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");
        worldname = (IworldsUtils.PlayerToUUID(pPlayer) + "-iWorld");

        // SELECT WORLD
        if (IworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Check si le monde existe déjà
        if (Sponge.getServer().getWorldProperties(worldname).isPresent()) {
            throw new CommandException(Text.of(TextColors.DARK_GRAY, worldname, Msg.keys.EXISTE_IWORLD), false);
        }

        File sourceFile = new File(ManageFiles.getPath() + "PATERN");
        File destFile = new File(fullpath);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            ie.printStackTrace();
            IworldsUtils.coloredMessage(pPlayer, Msg.keys.SQL);
            return CommandResult.success();
        }

        try {
            //WorldArchetype worldArchetype = WorldArchetype.builder().dimension(DimensionTypes.OVERWORLD).reset().build(worldname, worldname);
            WorldProperties worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
            Sponge.getServer().getWorldProperties(worldname).get().setLoadOnStartup(false);
            Sponge.getServer().getWorldProperties(worldname).get().getDimensionType().getId();
            Sponge.getServer().saveWorldProperties(worldProperties);
            Sponge.getGame().getServer().loadWorld(worldname);

        } catch (IOException ie) {
            ie.printStackTrace();
            IworldsUtils.coloredMessage(pPlayer, Msg.keys.SQL);
        }

        // INSERT
        if (!IworldsUtils.insertCreation(pPlayer, Msg.keys.SQL)) {
            return CommandResult.success();
        }

        // INSERT TRUST
        if (!IworldsUtils.insertTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
            return CommandResult.success();
        }

        // Configuration du monde
        Sponge.getServer().getWorld(worldname).get().setKeepSpawnLoaded(true);
        Sponge.getServer().getWorld(worldname).get().getWorldBorder().setCenter(0, 0);
        Sponge.getServer().getWorld(worldname).get().getWorldBorder().setDiameter(500);
        ;
        pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                .append(Text.of(Text.builder(Msg.keys.SUCCES_CREATION_1).color(TextColors.AQUA))).build()));
        IworldsLocations.teleport(pPlayer, worldname);
        pPlayer.sendTitle(IworldsUtils.titleSubtitle(Msg.keys.TITRE_BIENVENUE_1 + pPlayer.getName(), Msg.keys.TITRE_BIENVENUE_2));
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commandes de création des iWorlds"))
                .permission("iworlds.creation")
                .executor(new CreationCommande())
                .build();
    }
}

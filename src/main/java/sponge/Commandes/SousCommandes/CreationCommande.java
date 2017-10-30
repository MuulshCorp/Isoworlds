package sponge.Commandes.SousCommandes;

import common.Msg;
import sponge.IsoworldsSponge;
import sponge.Locations.IsoworldsLocations;
import sponge.Utils.IsoworldsUtils;
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
import java.util.NoSuchElementException;

/**
 * Created by Edwin on 05/10/2017.
 */

public class CreationCommande implements CommandExecutor {
    static final String INSERT = "INSERT INTO `isoworlds` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    static final String INSERT_TRUST = "INSERT INTO `autorisations` (`UUID_P`, `UUID_W`, `DATE_TIME`) VALUES (?, ?, ?)";
    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;
        IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL);
        IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        IsoworldsUtils.cm("IsoWorld name: " + worldname);
        // SELECT WORLD
        if (IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Check si le monde existe déjà
        if (Sponge.getServer().getWorldProperties(worldname).isPresent()) {
            return CommandResult.success();
        }

        File sourceFile = new File(ManageFiles.getPath() + "PATERN");
        File destFile = new File(fullpath);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            ie.printStackTrace();
            IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.SQL);
            return CommandResult.success();
        }


        try {
            //WorldArchetype worldArchetype = WorldArchetype.builder().dimension(DimensionTypes.OVERWORLD).reset().build(worldname, worldname);
            WorldProperties worldProperties = Sponge.getServer().createWorldProperties(worldname, WorldArchetypes.OVERWORLD);
            Sponge.getServer().getWorldProperties(worldname).get().setLoadOnStartup(false);
            Sponge.getServer().getWorldProperties(worldname).get().getDimensionType().getId();
            Sponge.getServer().saveWorldProperties(worldProperties);


            // INSERT
            if (IsoworldsUtils.insertCreation(pPlayer, Msg.keys.SQL)) {
                // INSERT TRUST
                if (IsoworldsUtils.insertTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
                    Sponge.getGame().getServer().loadWorld(worldname);
                    // Configuration du monde
                    Sponge.getServer().getWorld(worldname).get().setKeepSpawnLoaded(true);
                    Sponge.getServer().getWorld(worldname).get().getWorldBorder().setCenter(0, 0);
                    Sponge.getServer().getWorld(worldname).get().getWorldBorder().setDiameter(500);

                    Location<World> neutral = new Location<World>(Sponge.getServer().getWorld(worldname).get(), 0, 0, 0);
                    Location<World> firstspawn = IsoworldsLocations.getHighestLoc(neutral).orElse(null);
                    Sponge.getServer().getWorld(worldname).get().getProperties().setSpawnPosition(firstspawn.getBlockPosition());

                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder(Msg.keys.SUCCES_CREATION_1).color(TextColors.AQUA))).build()));
                    IsoworldsLocations.teleport(pPlayer, worldname);
                    pPlayer.sendTitle(IsoworldsUtils.titleSubtitle(Msg.keys.TITRE_BIENVENUE_1 + pPlayer.getName(), Msg.keys.TITRE_BIENVENUE_2));
                }
            }


        } catch (IOException | NoSuchElementException ie) {
            ie.printStackTrace();
            IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.SQL);
            return CommandResult.success();
        }
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commandes de création des iWorlds"))
                .permission("isoworlds.creation")
                .executor(new CreationCommande())
                .build();
    }
}
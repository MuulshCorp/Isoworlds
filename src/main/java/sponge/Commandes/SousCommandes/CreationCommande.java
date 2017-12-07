package sponge.Commandes.SousCommandes;

import common.Msg;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
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
import java.util.Optional;

import static sponge.Utils.IsoworldsUtils.isLocked;
import static sponge.Utils.IsoworldsUtils.setWorldProperties;

/**
 * Created by Edwin on 05/10/2017.
 */

public class CreationCommande implements CommandExecutor {
    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;
        IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        IsoworldsUtils.cm("IsoWorld name: " + worldname);

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        // SELECT WORLD
        if (IsoworldsUtils.iworldExists(pPlayer, Msg.keys.SQL)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_IWORLD).color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        // Check si le monde existe déjà
        if (Sponge.getServer().getWorldProperties(worldname).isPresent()) {
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        File sourceFile = new File(ManageFiles.getPath() + "PATERN");
        File destFile = new File(fullpath);

        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            ie.printStackTrace();
            IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.SQL);
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }


        // Création properties
        setWorldProperties(worldname, pPlayer);


        // INSERT
        if (IsoworldsUtils.insertCreation(pPlayer, Msg.keys.SQL)) {
            // INSERT TRUST
            if (IsoworldsUtils.insertTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
                // Chargement
                Sponge.getGame().getServer().loadWorld(worldname);

                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.SUCCES_CREATION_1).color(TextColors.AQUA))).build()));
                // Téléport
                IsoworldsLocations.teleport(pPlayer, worldname);
                pPlayer.sendTitle(IsoworldsUtils.titleSubtitle(Msg.keys.TITRE_BIENVENUE_1 + pPlayer.getName(), Msg.keys.TITRE_BIENVENUE_2));
            }
        }


        plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
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
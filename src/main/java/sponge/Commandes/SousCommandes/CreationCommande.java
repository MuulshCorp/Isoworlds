package sponge.Commandes.SousCommandes;

import common.Msg;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import sponge.IsoworldsSponge;
import sponge.Locations.IsoworldsLocations;
import sponge.Utils.IsoworldsUtils;
import common.ManageFiles;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.io.*;
import java.util.List;
import java.util.Optional;

import static sponge.Utils.IsoworldsUtils.setWorldProperties;

/**
 * Created by Edwin on 05/10/2017.
 */

public class CreationCommande implements CommandCallable {
    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult process(CommandSource source, String args) throws CommandException {

        // Variables
        String fullpath = "";
        String worldname = "";
        Player pPlayer = (Player) source;
        IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.CREATION_IWORLD);
        fullpath = (ManageFiles.getPath() + IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        worldname = (pPlayer.getUniqueId().toString() + "-IsoWorld");
        IsoworldsUtils.cm("IsoWorld name: " + worldname);
        String[] arg = args.split(" ");
        int size = arg.length;

        // SELECT WORLD
        if (IsoworldsUtils.isPresent(pPlayer, Msg.keys.SQL, false)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder(Msg.keys.EXISTE_IWORLD).color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Check si le monde existe déjà
        if (Sponge.getServer().getWorldProperties(worldname).isPresent()) {
            return CommandResult.success();
        }

        // Vérifie le nb argument
        if (size < 1) {
            pPlayer.sendMessage(Text.of(Text.builder("--------------------- [ ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("IsoWorlds ").color(TextColors.AQUA)))
                    .append(Text.of(Text.builder("] ---------------------").color(TextColors.GOLD)))
                    .build()));

            pPlayer.sendMessage(Text.of(Text.builder(" ").color(TextColors.GOLD).build()));

            // Soleil
            Text isoworld = Text.of(Text.builder("Sijania vous propose 4 types de IsoWorld:").color(TextColors.AQUA).build());
            pPlayer.sendMessage(isoworld);
            Text iw = Text.of(Text.builder("- FLAT/OCEAN/NORMAL/VOID: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("/iw creation [TYPE]").color(TextColors.AQUA))).build());
            pPlayer.sendMessage(iw);
            return CommandResult.success();
        }

        IsoworldsUtils.cm("DEBUGGGG: " + arg[0]);

        File sourceFile;
        switch (arg[0]) {
            case ("n"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-N/");
                IsoworldsUtils.cm("[TRACKING-IW] PATERN NORMAL: " + pPlayer.getName());
                break;
            case ("v"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-V/");
                IsoworldsUtils.cm("[TRACKING-IW] PATERN VOID: " + pPlayer.getName());
                break;
            case ("o"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-O/");
                IsoworldsUtils.cm("[TRACKING-IW] PATERN OCEAN: " + pPlayer.getName());
                break;
            case ("f"):
                sourceFile = new File(ManageFiles.getPath() + "PATERN-F/");
                IsoworldsUtils.cm("[TRACKING-IW] PATERN FLAT: " + pPlayer.getName());
                break;
            default:
                return CommandResult.success();
        }

        File destFile = new File(fullpath);


        try {
            ManageFiles.copyFileOrFolder(sourceFile, destFile);
        } catch (IOException ie) {
            ie.printStackTrace();
            IsoworldsUtils.coloredMessage(pPlayer, Msg.keys.SQL);
            return CommandResult.success();
        }


        // Création properties
        setWorldProperties(worldname, pPlayer);


        // INSERT
        if (IsoworldsUtils.setIsoWorld(pPlayer, Msg.keys.SQL)) {
            // INSERT TRUST
            if (IsoworldsUtils.setTrust(pPlayer, pPlayer.getUniqueId(), Msg.keys.SQL)) {
                // Chargement
                Sponge.getGame().getServer().loadWorld(worldname);

                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder(Msg.keys.SUCCES_CREATION_1).color(TextColors.AQUA))).build()));
                // Téléport
                IsoworldsLocations.teleport(pPlayer, worldname);
                pPlayer.sendTitle(IsoworldsUtils.titleSubtitle(Msg.keys.TITRE_BIENVENUE_1 + pPlayer.getName(), Msg.keys.TITRE_BIENVENUE_2));
            }
        }
        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return null;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return null;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return null;
    }
}
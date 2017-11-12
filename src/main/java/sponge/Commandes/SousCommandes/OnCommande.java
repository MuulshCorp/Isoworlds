package sponge.Commandes.SousCommandes;

import common.ManageFiles;
import common.Msg;
import org.spongepowered.api.world.World;
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
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Edwin on 15/10/2017.
 */
public class OnCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) source;
        worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");
        Boolean check = false;
        ArrayList<WorldProperties> worlds = new ArrayList<WorldProperties>();
        IsoworldsUtils.cm("check");

        for (WorldProperties world : Sponge.getServer().getUnloadedWorlds()) {
            if (world.getWorldName().contains("-IsoWorld")) {
                worlds.add(world);
            }
        }

        // loop iworlds
        for (WorldProperties world : worlds) {
            // si iworld existe
            if (world.getWorldName().equals(worldname.toString())) {
                // Si statut isoworld non présent (1)
                if (IsoworldsUtils.iworldPushed(worldname, Msg.keys.SQL)) {
                    IsoworldsUtils.cm("Debug 6");
                    // Création des chemins pour vérification
                    File file = new File(ManageFiles.getPath() + worldname);
                    File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");
                    File file3 = new File(ManageFiles.getPath() + worldname + "@PUSHED@PULL");
                    // Si Isoworld dossier présent (sans tag), on repasse le status à 0 (présent) et on continue
                    if (file.exists()) {
                        IsoworldsUtils.cm("Debug 7");
                        IsoworldsUtils.iworldSetStatus(worldname, 0, Msg.keys.SQL);
                        // Si le dossier est en @PULL et qu'un joueur le demande alors on le passe en @PULL
                        // Le script check ensutie
                    } else if (file2.exists()) {
                        ManageFiles.rename(ManageFiles.getPath() + worldname + "@PUSHED", "@PULL");
                        IsoworldsUtils.cm("PULL OK");
                        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania est sur le point de ramener votre IsoWorld dans ce royaume, veuillez patienter...").color(TextColors.GOLD)
                                .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
                        return CommandResult.success();
                    } else if (file3.exists()) {
                        pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania est sur le point de ramener votre IsoWorld dans ce royaume, veuillez patienter...").color(TextColors.GOLD)
                                .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
                        return CommandResult.success();
                    }
                }

                check = true;
                IsoworldsUtils.cm("check");
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("Sijania vient d'activer votre IsoWorld.").color(TextColors.AQUA))).build()));
                // Si ça réussi
                try {
                    Sponge.getServer().loadWorld(worldname.toString());
                    return CommandResult.success();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania n'est pas parvenue à charger votre IsoWorld.").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
                }

                // si iworld n'existe pas
                if (check == false) {
                    pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                            .append(Text.of(Text.builder("Sijania ne repère aucun IsoWorld à votre nom dans le Royaume Isolonice. Entrez /iw creation pour en obtenir un.").color(TextColors.AQUA))).build()));
                    return CommandResult.success();
                }
            }
        }
        IsoworldsUtils.cm("finished");
        return CommandResult.success();
    }


    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour activer son IsoWorld."))
                .permission("isoworlds.activer")
                .executor(new OnCommande())
                .build();
    }
}
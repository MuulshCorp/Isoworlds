package sponge.Commandes.SousCommandes;

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

import java.util.ArrayList;

import static sponge.Utils.IsoworldsUtils.isLocked;
import static sponge.Utils.IsoworldsUtils.setWorldProperties;

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

        // Si la méthode renvoi vrai alors on return car le lock est défini, sinon elle le set auto
        if (isLocked(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        // Import / Export
        if (!IsoworldsUtils.checkTag(pPlayer, worldname)) {
            // Suppression lock
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        // Si isoworld déjà chargé
        if (Sponge.getServer().getWorld(worldname).isPresent()) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania indique que votre IsoWorld est déjà chargé.").color(TextColors.AQUA))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
            return CommandResult.success();
        }

        for (WorldProperties world : Sponge.getServer().getUnloadedWorlds()) {
            if (world.getWorldName().contains("-IsoWorld")) {
                worlds.add(world);
            }
        }

        // loop iworlds
        for (WorldProperties world : worlds) {
            // si iworld existe
            if (world.getWorldName().equals(worldname.toString())) {
                if (!IsoworldsUtils.checkTag(pPlayer, worldname)) {
                    plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                    return CommandResult.success();
                }

                check = true;
                IsoworldsUtils.cm("check");
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                        .append(Text.of(Text.builder("Sijania vient d'activer votre IsoWorld.").color(TextColors.AQUA))).build()));
                // Si ça réussi
                try {
                    setWorldProperties(worldname, pPlayer);
                    Sponge.getServer().loadWorld(worldname.toString());
                    plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
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
                    plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
                    return CommandResult.success();
                }
            }
        }
        IsoworldsUtils.cm("finished");
        plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
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
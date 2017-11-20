package sponge.Commandes.SousCommandes;

import com.google.common.collect.Iterables;
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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static sponge.Utils.IsoworldsUtils.isSetCooldown;

/**
 * Created by Edwin on 10/10/2017.
 */
public class ListeCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        Player pPlayer = (Player) source;
        ArrayList<World> worlds = new ArrayList<World>();
        Boolean check = false;
        for(World world : Sponge.getServer().getWorlds()) {
            if (world.isLoaded()) {
                if (world.getName().contains("-IsoWorld")) {
                    worlds.add(world);
                }
            }
        }

        // Check si isoworld existe
        if (check == true) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania ne repère aucun IsoWorld dans le Royaume Isolonice").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }

        // Si la méthode renvoi vrai alors on return car le cooldown est défini, sinon elle le set auto
        if (isSetCooldown(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }

        // Construction des textes en fonction des isoworlds loadés
        Text title = Text.of(Text.builder("[Liste des IsoWorlds (cliquables)]").color(TextColors.GOLD).build());
        pPlayer.sendMessage(title);
        for(World w : worlds ) {
            String worldname = w.getName();
            String[] split = w.getName().split("-IsoWorld");
            UUID uuid = UUID.fromString(split[0]);
            UserStorageService userStorage = Sponge.getServiceManager().provide(UserStorageService.class).get();
            Optional<User> player = userStorage.get(uuid);
            String pname;
            String status;
            if (player.isPresent()) {
                pname = player.get().getName();
            } else {
                pname = "OFF";
            }

            if (player.get().isOnline()) {
                status = "ON";
            } else {
                status = "OFF";
            }

            int numOfEntities = w.getEntities().size();
            int loadedChunks = Iterables.size(w.getLoadedChunks());

            Text name = Text.of(Text.builder(pname + " [" + status +"] | Chunks: " + loadedChunks + " | Entités: " + numOfEntities)
                    .color(TextColors.GREEN)
                    .append(Text.builder(" | TPS: " + Sponge.getServer().getTicksPerSecond())
                            .color(IsoworldsUtils.getTPS(Sponge.getServer().getTicksPerSecond()).getColor()).build())
                    .onClick(TextActions.runCommand("/iw teleport " + pPlayer.getName().toString() + " " + worldname))
                    .onHover(TextActions.showText(Text.of(worldname))).build());
            pPlayer.sendMessage(name);
        }
        plugin.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour lister les iWorlds"))
                .permission("isoworlds.liste")
                .executor(new ListeCommande())
                .build();
    }
}
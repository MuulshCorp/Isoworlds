package isolonice.iworlds.Commandes.SousCommandes;

import com.google.common.collect.Iterables;
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
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Edwin on 10/10/2017.
 */
public class ListeCommande implements CommandExecutor {

    private final IworldsSponge plugin = IworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        Player pPlayer = (Player) source;
        ArrayList<World> worlds = new ArrayList<World>();
        Boolean check = false;
        for(World world : Sponge.getServer().getWorlds()) {
            if (world.isLoaded()) {
                if (world.getName().contains("-iWorld")) {
                    worlds.add(world);
                }
            }
        }

        if (check == true) {
            pPlayer.sendMessage(Text.of(Text.builder("[iWorlds]: ").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("Sijania ne repère aucun iWorld dans le Royaume Isolonice").color(TextColors.AQUA))).build()));
            return CommandResult.success();
        }
        Text title = Text.of(Text.builder("[Liste des iWorlds (cliquables)]").color(TextColors.GOLD).build());
        pPlayer.sendMessage(title);
        for(World w : worlds ) {
            String worldname = w.getName();
            String[] split = w.getName().split("-iWorld");
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
                            .color(IworldsUtils.getTPS(Sponge.getServer().getTicksPerSecond()).getColor()).build())
                    .onClick(TextActions.runCommand("/iw teleport " + pPlayer.getName() + " " + worldname))
                    .onHover(TextActions.showText(Text.of(worldname))).build());
            pPlayer.sendMessage(name);
        }
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande pour lister les iWorlds"))
                .permission("iworlds.liste")
                .executor(new ListeCommande())
                .build();
    }
}
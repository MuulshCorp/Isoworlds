package sponge.Commandes.SousCommandes;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import sponge.IsoworldsSponge;

import static sponge.IsoworldsSponge.instance;

/**
 * Created by Edwin on 20/11/2017.
 */
public class BiomeCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        return CommandResult.success();
    }

    // Constructeurs
    public static CommandSpec getCommand() {
        return CommandSpec.builder()
                .description(Text.of("Commande permettant de changer de biome dans son IsoWorld."))
                .permission("isoworlds.biome")
                .executor(new BiomeCommande())
                .build();
    }
}
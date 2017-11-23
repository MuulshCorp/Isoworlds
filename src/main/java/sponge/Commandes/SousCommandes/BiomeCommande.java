package sponge.Commandes.SousCommandes;

import common.Msg;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

import static sponge.IsoworldsSponge.instance;
import static sponge.Utils.IsoworldsUtils.isSetCooldown;

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
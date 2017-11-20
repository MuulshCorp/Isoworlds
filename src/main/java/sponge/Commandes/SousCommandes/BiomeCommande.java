package sponge.Commandes.SousCommandes;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import sponge.IsoworldsSponge;
import sponge.Utils.IsoworldsUtils;

import static sponge.Utils.IsoworldsUtils.isSetCooldown;

/**
 * Created by Edwin on 20/11/2017.
 */
public class BiomeCommande implements CommandExecutor {

    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {

        // Variables
        String worldname = "";
        Player pPlayer = (Player) source;
        worldname = (IsoworldsUtils.PlayerToUUID(pPlayer) + "-IsoWorld");

        // Si la méthode renvoi vrai alors on return car le cooldown est défini, sinon elle le set auto
        if (isSetCooldown(pPlayer, String.class.getName())) {
            return CommandResult.success();
        }



        IsoworldsUtils.cm("finished");
        plugin.cooldown.remove(pPlayer.getUniqueId().toString() + ";" + String.class.getName());
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
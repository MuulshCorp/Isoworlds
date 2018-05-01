package sponge.Utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.IsoworldsSponge;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Created by Edwin on 19/11/2017.
 */
public class IsoWorldsTasks implements Consumer<Task> {

    private int check = 60;
    private Player pPlayer;
    private File file;
    private final IsoworldsSponge plugin = IsoworldsSponge.instance;

    public IsoWorldsTasks(Player pPlayer, File file) {

        this.pPlayer = pPlayer;
        this.file = file;
    }


    @Override
    public void accept(Task task) {
        // Message de démarrage process
        if (check == 60) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania est sur le point de ramener votre IsoWorld dans ce royaume, veuillez patienter... (Temps estimé: 60 secondes)").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
        }
        check --;
        if (check < 1) {
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania ne parvient pas à charger votre monde, veuillez re tenter ou contacter l'équipe Isolonice.").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.RED))).build()));
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");
            task.cancel();
        } else if (file.exists()) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania vient de terminer son travail, l'IsoWorld est disponible !").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));

            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");
            task.cancel();
        }
    }
}


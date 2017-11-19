package sponge.Utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.Locations.IsoworldsLocations;

import java.io.File;
import java.util.function.Consumer;

/**
 * Created by Edwin on 19/11/2017.
 */
public class IsoWorldsTasks implements Consumer<Task> {

    private int check = 15;
    private Player pPlayer;
    private File file;

    public IsoWorldsTasks(Player pPlayer, File file) {

        this.pPlayer = pPlayer;
        this.file = file;
    }


    @Override
    public void accept(Task task) {
        check --;
        if (check < 1) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania ne parvient pas à charger votre monde, veuillez re tenter ou contacter l'équipe Isolonice.").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.RED))).build()));
            task.cancel();
        } else if (file.exists()) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania vient de terminer son travail, l'IsoWorld est disponible !").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));

            task.cancel();
        }
    }
}


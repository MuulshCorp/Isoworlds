package sponge.Utils;

import common.Msg;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.IsoworldsSponge;

import java.io.File;
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
            // Notification au joueur qu'il doit patienter
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania est sur le point de ramener votre IsoWorld dans ce royaume, veuillez patienter... (Temps estimé: 60 secondes)").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
        }
        check --;
        // Si inférieur à 1 alors tout le temps s'est écoulé sans que le IsoWorld soit présent en fichier
        if (check < 1) {
            // Notification au joueur de contacter l'équipe
                pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania ne parvient pas à charger votre monde, veuillez re tenter ou contacter l'équipe Isolonice.").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.RED))).build()));
            // Suppression du TAG pour permettre l'utilisation de la commande maison et confiance access
            plugin.lock.remove(file.getName() + ";" + file.getName());
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");
            task.cancel();
        // Si le dossier existe, alors on repasse le statut à 0 en BDD (présent)
        } else if (file.exists()) {
            // Passage du IsoWorld en statut présent
            IsoworldsUtils.setStatus(file.getName(), 0, Msg.keys.SQL);

            // Notification au joueur que le IsoWorld est disponible
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania vient de terminer son travail, l'IsoWorld est disponible !").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));

            // Suppression du TAG pour permettre l'utilisation de la commande maison et confiance access
            plugin.lock.remove(file.getName() + ";" + file.getName());
            plugin.lock.remove(pPlayer.getUniqueId().toString() + ";" + "checkTag");
            task.cancel();
        }
    }
}


/*
 * This file is part of IsoWorlds, licensed under the MIT License (MIT).
 *
 * Copyright (c) Edwin Petremann <https://github.com/Isolonice/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sponge.util.task.SAS;

import common.Msg;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.Main;
import sponge.util.action.StorageAction;

import java.io.File;
import java.util.function.Consumer;

public class Pull implements Consumer<Task> {

    private int check = 60;
    private Player pPlayer;
    private File file;
    private final Main plugin = Main.instance;

    public Pull(Player pPlayer, File file) {

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
            StorageAction.setStatus(file.getName(), 0);

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


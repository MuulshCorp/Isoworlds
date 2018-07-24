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

import common.ManageFiles;
import common.Msg;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;
import sponge.util.action.StorageAction;
import sponge.util.console.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Push {

    public static void PushProcess(Integer time) {

        Map<String, Integer> worlds = new HashMap<String, Integer>();
        int x = time;

        // IsoWorlds task unload
        Task.builder().execute(() -> {
            // Démarrage de la procédure, on log tout les élements du map à chaque fois
            Logger.warning("Démarrage de l'analayse des IsoWorlds vides pour déchargement...");
            if (worlds.isEmpty()) {
                Logger.info("IsoWorlds inactifs à l'analyse précédente: Aucun");
            } else {
                Logger.info("IsoWorlds inactifs à l'analyse précédente:");
                for (Map.Entry<String, Integer> entry : worlds.entrySet()) {
                    Logger.info("- " + entry);
                }
            }
            // Boucle de tous les mondes
            for (World world : Sponge.getServer().getWorlds()) {
                // Si le monde est chargé et contient IsoWorld
                if (world.isLoaded() & world.getName().contains("-IsoWorld")) {

                    // Si le nombre de joueurs == 0
                    if (world.getPlayers().size() == 0) {
                        // Si le monde n'est pas présent dans le tableau
                        if (worlds.get(world.getName()) == null) {
                            worlds.put(world.getName(), 1);
                            Logger.warning(world.getName() + " vient d'être ajouté à l'analyse");
                        } else {
                            // Sinon on incrémente
                            worlds.put(world.getName(), worlds.get(world.getName()) + 1);
                        }

                        // Si le nombre est supérieur ou = à X on unload
                        if (worlds.get(world.getName()) >= x) {
                            Logger.info("La valeur de: " + world.getName() + " est de " + x + " , déchargement...");
                            // Procédure de déchargement //

                            // Sauvegarde du monde et déchargement
                            try {
                                Sponge.getServer().getWorld(world.getName()).get().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                                continue;
                            }

                            // Save & unload AUTO CORRECT
                            try {
                                world.save();
                                // If is mirrored then unload + remove then return
                                if (StorageAction.isMirrored(world.getName()) == 1) {
                                    Logger.severe("--- Anomalie détectée, unload interrompu et suppression de l'anomalie: " + world.getName() + " ---");
                                    Sponge.getServer().unloadWorld(world);
                                    Sponge.getServer().deleteWorld(world.getProperties());
                                    Logger.severe("--- Anomalie: Corrigée, suppression effectuée avec succès de l'isoworld: " + world.getName() + " ---");
                                    continue;
                                } else {
                                    if (!Sponge.getServer().unloadWorld(world)) {
                                        Logger.severe("--- Echec du déchargement de l'IsoWorld: " + world.getName() + " ---");
                                        continue;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }

                            // Suppression dans le tableau
                            worlds.remove(world.getName());

                            // Vérification du statut du monde, si il est push ou non
                            if (!StorageAction.getStatus(world.getName())) {
                                Logger.info("debug 1");
                                File check = new File(ManageFiles.getPath() + world.getName());
                                // Si le dossier existe alors on met le statut à 1 (push)
                                if (check.exists()) {
                                    Logger.info("debug 2");
                                    StorageAction.setStatus(world.getName(), 1, Msg.keys.SQL);

                                    // Suppression ID
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat_old"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/session.lock"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/forcedchunks.dat"));

                                    // Tag du dossier en push
                                    ManageFiles.rename(ManageFiles.getPath() + world.getName(), "@PUSH");
                                    Logger.info("- " + world.getName() + " : PUSH avec succès");

                                    // Suppression du monde
                                    try {
                                        if (Sponge.getServer().deleteWorld(world.getProperties()).get()) {
                                            Logger.info("- " + world.getName() + " : Isoworld supprimé avec succès !");
                                        }
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                // Sinon on continue la boucle
                                continue;
                            }
                        }
                        // Si le nombre de joueur est supérieur à 0, purge le tableau du IsoWorld
                    } else if (worlds.get(world.getName()) != null) {
                        worlds.remove(world.getName());
                        Logger.warning(world.getName() + " de nouveau actif, supprimé de l'analyse");
                    }
                }
            }
            if (worlds.isEmpty()) {
                Logger.info("Aucun IsoWorld n'est à " + x + " minutes d'inactivité...");
                Logger.warning("Fin de l'analyse");
            } else {
                Logger.info("Les IsoWorlds vides depuis " + x + " minutes viennent d'être déchargés");
                Logger.warning("Fin de l'analyse");
            }
        }).interval(1, TimeUnit.MINUTES).
                name("Analyse des IsoWorlds vides...").
                submit(sponge.Main.instance);
    }
}

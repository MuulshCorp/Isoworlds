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
package sponge.util.action;

import common.ManageFiles;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import sponge.Main;
import sponge.util.console.Logger;
import sponge.util.task.SAS.Pull;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import static sponge.util.action.LockAction.checkLockFormat;

public class StorageAction {

    public static final Main plugin = Main.instance;

    // Check tag of pPlayer IsoWorld (@PUSH, @PUSHED, @PULL, @PULLED, @PUSHED@PULL, @PUSHED@PULLED)
    public static Boolean checkTag(Player pPlayer, String worldname) {

        // Création des chemins pour vérification
        File file = new File(ManageFiles.getPath() + worldname);
        File file2 = new File(ManageFiles.getPath() + worldname + "@PUSHED");

        // Si vrai alors en état @PUSHED en bdd
        if (StorageAction.getStatus(worldname)) {
            Logger.info("ISOWORLD: " + worldname + " EN ETAT @PUSHED");

            // !! Gestion des anomalies !!
            // Si le dossier sans tag et avec tag existe, alors il a été accédé par un moyen tier et on supprime le non TAG
            if (file.exists()) {
                if (file2.exists()) {
                    Logger.warning(" --- Anomalie (@PUSHED: Dossier isoworld et isoworld tag tous deux présents pour: " + worldname + " ---");
                    // Déchargement au cas ou
                    if (Sponge.getServer().getWorld(worldname).isPresent()) {
                        Sponge.getServer().unloadWorld(Sponge.getServer().getWorld(worldname).get());
                        Logger.warning(" --- Anomalie (@PUSHED: Déchargement du IsoWorld anormalement chargé: " + worldname + " ---");
                    }
                    // Suppression du dossier
                    ManageFiles.deleteDir(file);
                }
            }

            // !! Démarrage de la procédure de récupération !!
            // Si dossier @PUSHED alors on le met en @PULL pour lancer la procédure de récupération
            if (file2.exists()) {
                Logger.info("PASSAGE EN @PULL: " + worldname);
                ManageFiles.rename(ManageFiles.getPath() + worldname + "@PUSHED", "@PULL");
                Logger.info("PASSAGE EN @PULL OK: " + worldname);

                // Vérification pour savoir si le dossier existe pour permettre au joueur d'utiliser la cmd maison
                // On retourne faux pour dire que le monde n'est pas disponible
                // Le passage du statut à NON @PUSHED se fait dans la task
                Task task = Task.builder()
                        .execute(new Pull(pPlayer, file))
                        .async()
                        .interval(1, TimeUnit.SECONDS)
                        .name("Self-Cancelling Timer Task")
                        .submit(plugin);
            } else {
                // Gestion du cas ou le dossier IsoWorld ne serait pas présent alors qu'il est @PUSHED en bdd
                Logger.warning(" --- Anomalie (@PUSHED): Dossier isoworld tag n'existe pas: " + worldname + " ---");
            }

            // Retourner faux pour indiquer que le dossier n'existe pas, il doit être en procédure
            return false;

        } else if (!StorageAction.getStatus(worldname)) {
            Logger.info("ISOWORLD DISPONIBLE: " + worldname + " - ETAT NON @PUSHED");

            // Vérification si le dossier @PUSHED n'existe pas, on le supprime dans ce cas, anomalie
            if (file2.exists()) {
                ManageFiles.deleteDir(file2);
                Logger.info(": " + worldname);
                Logger.warning(" --- Anomalie (NON @PUSHED): Dossier isoworld et isoworld tag tous deux présents pour: " + worldname + " ---");
            }

            // IsoWorld disponible, retour vrai
            return true;
        } else {
            // Si ni @PUSHED ni NON @PUSHED en BDD alors on retourne faux car il doit y avoir un gros problème :)
            Logger.warning(" --- Anomalie (NI @PUSHED NI NON @PUSHE): IsoWorld: " + worldname + " ---");
            return false;
        }
    }

    // Check status of a IsoWorld, if is Pushed return true, else return false
    public static Boolean getStatus(String world) {
        String CHECK = "SELECT STATUS FROM `isoworlds` WHERE `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // UUID_W
            check_w = world;
            check.setString(1, check_w);
            // SERVEUR_ID
            check.setString(2, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
            Logger.info(check.toString());
            Logger.info("Debug 8");
            if (rselect.next()) {
                Logger.info(rselect.toString());
                Logger.info("Debug 9");
                if (rselect.getInt(1) == 1) {
                    Logger.info("Debug 10");
                    return true;
                } else {
                    return false;
                }

            }
        } catch (Exception se) {
            se.printStackTrace();
            return false;
        }
        return false;
    }

    // Set global status
    public static Boolean setGlobalStatus() {
        String CHECK = "UPDATE `isoworlds` SET `status` = 1 WHERE `server_id` = ?";
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // SERVEUR_ID
            check.setString(1, plugin.servername);
            // Requête
            ResultSet rselect = check.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Set status of IsoWorld (1 for Pushed, 0 for Present)
    // It returns true if pushed, false si envoyé ou à envoyer
    public static void setStatus(String world, Integer status, String messageErreur) {
        String CHECK = "UPDATE `isoworlds` SET `status` = ? WHERE `uuid_w` = ? AND `server_id` = ?";
        String check_w;
        try {
            PreparedStatement check = plugin.database.prepare(CHECK);

            // STATUS
            check.setInt(1, status);
            // UUID_W
            check_w = (world);
            check.setString(2, check_w);
            // SERVEUR_ID
            check.setString(3, plugin.servername);
            // Requête
            Logger.info("Debug 3: " + check.toString());
            check.executeUpdate();
        } catch (Exception se) {
            se.printStackTrace();
            Logger.severe(messageErreur);
        }
    }

    // Vérifie si le lock est présent et renvoi vrai, sinon défini le lock et renvoi false
    public static Boolean iwInProcess(Player pPlayer, String worldname) {
        // Si le lock est set, alors on renvoie false avec un message de sorte à stopper la commande et informer le jouer
        if (checkLockFormat(worldname, worldname)) {
            pPlayer.sendMessage(Text.of(Text.builder("[IsoWorlds]: Sijania indique que vous devez patienter avant de pouvoir utiliser de nouveau cette commande.").color(TextColors.GOLD)
                    .append(Text.of(Text.builder("").color(TextColors.AQUA))).build()));
            return true;
        } else {
            // On set lock
            plugin.lock.put(worldname + ";" + worldname, 1);
            return false;
        }
    }

    // Check if mirror iw
    //0 normal - 1 anomally
    public static int isMirrored(String worldname) {
        // Check if file exist, to detect mirrors
        File file = new File(ManageFiles.getPath() + "/" + worldname + "@PUSHED");
        File file2 = new File(ManageFiles.getPath() + "/" + worldname);
        // If exists and contains Isoworld
        if (file.exists() & file2.exists() & worldname.contains("-IsoWorld")) {
            return 1;
        }
        return 0;
    }
}

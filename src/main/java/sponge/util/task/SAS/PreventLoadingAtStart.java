/*
 * This file is part of Isoworlds, licensed under the MIT License (MIT).
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
import org.slf4j.Logger;
import org.spongepowered.api.scheduler.Task;
import sponge.Main;
import sponge.configuration.Configuration;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class PreventLoadingAtStart {

    static Logger logger = Main.instance.getLogger();

    public static void move() {
        String name = "";

        // Isoworlds-SAS
        logger.info("[Isoworlds-SAS]: Stockage des Isoworlds un tag dans le SAS");
        File dest = new File(ManageFiles.getPath() + "/Isoworlds-SAS/");
        File source = new File(ManageFiles.getPath());
        // Retourne la liste des Isoworld tag
        for (File f : ManageFiles.getOutSAS(new File(source.getPath()))) {
            ManageFiles.deleteDir(new File(f.getPath() + "/level_sponge.dat"));
            ManageFiles.deleteDir(new File(f.getPath() + "/level_sponge.dat_old"));
            // Gestion des Isoworlds non push, si ne contient pas de tag alors "PUSH-SAS" et on le renomme lors de la sortie
            if (ManageFiles.move(source + "/" + f.getName(), dest.getPath())) {
                // Si le dossier n'est pas TAG et que le dossier de ce même nom avec TAG n'existe pas
                if (!f.getName().contains("@PUSHED")) {
                    // Si le Isoworld possède pas de @PUSHED dans le dossier de base ou le SAS alors on supprime
                    if ((new File(ManageFiles.getPath() + "Isoworlds-SAS/" + f.getName() + "@PUSHED").exists())
                            || (new File(f.getPath() + f.getName() + "@PUSHED").exists())) {
                        ManageFiles.deleteDir(new File(ManageFiles.getPath() + "Isoworlds-SAS/" + f.getName()));
                        logger.info("[Isoworlds-SAS: Anomalie sur le Isoworld " + f.getName());
                        continue;
                    }
                    // Tag Isoworlds @PUSH if Storage config enabled
                    if (Configuration.getStorage()) {
                        ManageFiles.rename(ManageFiles.getPath() + "Isoworlds-SAS/" + f.getName(), "@PUSH");
                    }
                    logger.info("[Isoworlds-SAS]: Isoworlds désormais TAG à PUSH");
                }
            } else {
                logger.info("[Isoworlds-SAS]: Echec de stockage > " + name);
            }
        }
    }

    public static void moveBack() {
        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                sponge.util.console.Logger.info("[Isoworlds-SAS]: Remise en place des Isoworlds dans le SAS");
                File source = new File(ManageFiles.getPath() + "/Isoworlds-SAS/");
                File dest = new File(ManageFiles.getPath());
                // Retourne la liste des Isoworld tag
                for (File f : ManageFiles.getOutSAS(new File(source.getPath()))) {
                    // Gestion des Isoworlds non push, si ne contient pas de tag
                    if (ManageFiles.move(source + "/" + f.getName(), dest.getPath())) {
                        logger.info("[Isoworlds-SAS]: " + f.getName() + " retiré du SAS");
                    } else {
                        logger.info("[Isoworlds-SAS]: Echec de destockage > " + f.getName());
                    }
                }
            }
        }).
                delay(1, TimeUnit.SECONDS).
                name("Remet les Isoworlds hors du SAS.").submit(Main.instance);
    }
}

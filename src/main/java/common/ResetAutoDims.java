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
package common;

import bukkit.util.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class ResetAutoDims {
    // reset worlds
    public static void reset() {

        //Date du jour
        Timestamp todayTimestamp = new Timestamp(System.currentTimeMillis() / 1000);

        //Next friday
        Calendar nextFriday = Calendar.getInstance();

        int weekday = nextFriday.get(Calendar.DAY_OF_WEEK);
        if (weekday != Calendar.FRIDAY)
        {
            // calculate how much to add
            // the 2 is the difference between Saturday and Monday
            int days = (Calendar.SATURDAY - weekday + 6) % 7;
            nextFriday.add(Calendar.DAY_OF_YEAR, days);
        }
        nextFriday.set(Calendar.HOUR_OF_DAY, 19);
        nextFriday.set(Calendar.MINUTE, 0);
        nextFriday.set(Calendar.SECOND, 0);
        nextFriday.set(Calendar.MILLISECOND, 0);

        //Next friday timestamp
        Date tempFriday = nextFriday.getTime();
        String nextFridayTimeStamp = String.valueOf((new Timestamp(tempFriday.getTime())).getTime() / 1000);
        Logger.warning("--- Prochaine date de réinitialisation des dimensions: " + nextFriday.getTime());

        String[] dims = new String[]{"exploration", "DIM-1", "DIM1", "minage"};

        // Check if file exists
        try {
            for (String dim : dims) {
                File dimFile = new File(ManageFiles.getPath() + "/" + dim);
                File resetFile = new File(ManageFiles.getPath() + "/" + dim + "/reset.txt");

                if (!dimFile.exists()) {
                    Logger.warning("--- Le dossier de réinitialisation pour la dimension " + dim + " n'éxiste pas");
                    continue;
                }

                if (!resetFile.exists()) {
                    FileWriter out = new FileWriter(resetFile);
                    out.write(nextFridayTimeStamp);
                    out.close();
                    Logger.warning("--- Le fichier de réinitialisation pour la dimension " + dim + " n'éxiste pas, création...");
                } else {
                    String nextResetDate = Files.readAllLines(Paths.get(resetFile.toURI())).get(0);
                    Logger.warning("--- Timestamp enregistré dans le fichier: " + nextResetDate
                            + " | Timestamp du jour: " + todayTimestamp.getTime() + " " + dim);
                    // Exploration
                    if (todayTimestamp.after(new Timestamp(Long.valueOf(nextResetDate)))) {
                        Logger.warning("--- Démarrage de la réinitialisation pour la dimension " + dim);
                        ManageFiles.deleteDir(dimFile);
                        dimFile.mkdir();
//                        FileWriter out = new FileWriter(resetFile);
//                        out.write(nextFridayTimeStamp);
//                        out.close();
                        Logger.warning("--- Réinitialisation avec succès de la dimension " + dim);
                    }
                }

            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}

package sponge.Utils;

import common.ManageFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Edwin on 29/04/2018.
 */
public class IsoWorldsResetDim {
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
        IsoworldsLogger.warning("--- Prochaine date de réinitialisation des dimensions: " + nextFriday.getTime());

        String[] dims = new String[]{"exploration", "DIM-1", "DIM1", "minage"};

        // Check if file exists
        try {
            for (String dim : dims) {
                File dimFile = new File(ManageFiles.getPath() + "/" + dim);
                File resetFile = new File(ManageFiles.getPath() + "/" + dim + "/reset.txt");

                if (!dimFile.exists()) {
                    IsoworldsLogger.warning("--- Le dossier de réinitialisation pour la dimension " + dim + " n'éxiste pas");
                    continue;
                }

                if (!resetFile.exists()) {
                    FileWriter out = new FileWriter(resetFile);
                    out.write(nextFridayTimeStamp);
                    out.close();
                    IsoworldsLogger.warning("--- Le fichier de réinitialisation pour la dimension " + dim + " n'éxiste pas, création...");
                } else {
                    String nextResetDate = Files.readAllLines(Paths.get(resetFile.toURI())).get(0);
                    IsoworldsLogger.warning("--- Timestamp enregistré dans le fichier: " + nextResetDate
                            + " | Timestamp du jour: " + todayTimestamp.getTime() + " " + dim);
                    // Exploration
                    if (todayTimestamp.after(new Timestamp(Long.valueOf(nextResetDate)))) {
                        IsoworldsLogger.warning("--- Démarrage de la réinitialisation pour la dimension " + dim);
                        ManageFiles.deleteDir(dimFile);
                        dimFile.mkdir();
                        IsoworldsLogger.warning("--- Réinitialisation avec succès de la dimension " + dim);
                    }
                }

            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}

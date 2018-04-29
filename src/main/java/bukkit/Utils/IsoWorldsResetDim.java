package bukkit.Utils;

/**
 * Created by Edwin on 29/04/2018.
 */

import common.ManageFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class IsoWorldsResetDim {
    // reset worlds
    public static void reset() {

        //Date du jour
        Timestamp todayTimestamp = new Timestamp(System.currentTimeMillis() / 1000);

        //Next friday
        Calendar nextFriday = Calendar.getInstance();
        nextFriday.set(Calendar.HOUR_OF_DAY, 19);
        nextFriday.set(Calendar.MINUTE, 0);
        nextFriday.set(Calendar.SECOND, 0);
        nextFriday.set(Calendar.MILLISECOND, 0);
        nextFriday.add(Calendar.DAY_OF_WEEK, 5);

        //Next friday timestamp
        Date tempFriday = nextFriday.getTime();
        String nextFridayTimeStamp = String.valueOf((new Timestamp(tempFriday.getTime())).getTime() / 1000);
        sponge.Utils.IsoworldsLogger.warning("--- Prochaine date de réinitialisation des dimensions: " + nextFriday.getTime());

        String[] dims = new String[]{"exploration", "DIM-1", "DIM1"};

        // Check if file exists
        try {
            for (String dim : dims) {
                File dimFile = new File(ManageFiles.getPath() + "/" + dim);
                File resetFile = new File(ManageFiles.getPath() + "/" + dim + "/reset.txt");
                File region = new File(ManageFiles.getPath() + "/" + dim + "/region");

                if (!dimFile.exists()) {
                    FileWriter out = new FileWriter(resetFile);
                    out.write(nextFridayTimeStamp);
                    out.close();
                    sponge.Utils.IsoworldsLogger.warning("--- Le fichier de réinitialisation pour la dimension " + dim + " n'éxiste pas, création...");
                } else {
                    String nextResetDate = Files.readAllLines(Paths.get(resetFile.toURI())).get(0);
                    sponge.Utils.IsoworldsLogger.warning("--- Timestamp enregistré dans le fichier: " + nextResetDate
                            + " | Timestamp du jour: " + todayTimestamp.getTime() + " " + dim);
                    // Exploration
                    if (todayTimestamp.after(new Timestamp(Long.valueOf(nextResetDate)))) {
                        sponge.Utils.IsoworldsLogger.warning("--- Démarrage de la réinitialisation pour la dimension " + dim);
                        ManageFiles.deleteDir(region);
                        resetFile.delete();
                        FileWriter out = new FileWriter(resetFile);
                        out.write(nextFridayTimeStamp);
                        out.close();
                        sponge.Utils.IsoworldsLogger.warning("--- Réinitialisation avec succès de la dimension " + dim);
                    }
                }

            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }
}

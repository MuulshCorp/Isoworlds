package common;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin on 20/10/2017.
 */
public class ManageFiles {

    public static void copyFileOrFolder(File source, File dest, CopyOption... options) throws IOException {
        if (source.isDirectory())
            copyFolder(source, dest, options);
        else {
            ensureParentFolder(dest);
            copyFile(source, dest, options);
        }
    }

    private static void copyFolder(File source, File dest, CopyOption... options) throws IOException {
        if (!dest.exists())
            dest.mkdirs();
        File[] contents = source.listFiles();
        if (contents != null) {
            for (File f : contents) {
                File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
                if (f.isDirectory())
                    copyFolder(f, newFile, options);
                else
                    copyFile(f, newFile, options);
            }
        }
    }

    private static void copyFile(File source, File dest, CopyOption... options) throws IOException {
        java.nio.file.Files.copy(source.toPath(), dest.toPath(), options);
    }

    private static void ensureParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();
    }

    // Move directory
    public static boolean move(String source, String dest) {
        // File (or Directory) to be moved
        File file = new File(source);

        // Destination directory
        File dir = new File(dest);

        // Move file to a new directory
        boolean success = file.renameTo(new File(dir, file.getName()));

        if (success) {
            return true;
        } else {
            return false;
        }
    }

    // Récupère la liste des dossiers tag
    // Si contient @ alors add to list et retourne
    public static List<File> getOutSAS(File currDir) {
        List<File> dirs = new ArrayList<>();
        for (File file : currDir.listFiles()) {
            if (file.isDirectory() & file.getName().contains("-IsoWorld")) {
                dirs.add(file);
            }
        }
        return dirs;
    }

    // Renommer dossier
    public static Boolean rename(String path, String newname) {
        File file = new File(path);
        if (!file.isDirectory()) {
            return false;
        }
        file.renameTo(new File(path + newname));
        return true;
    }

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    // Récupération du chemin racine
    public static String getPath() {
        String path = (System.getProperty("user.dir") + "/Isolonice/");
        return path;
    }

}

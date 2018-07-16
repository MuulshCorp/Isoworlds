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
package bukkit;

import bukkit.command.Commands;
import bukkit.listener.Listeners;
import bukkit.location.Locations;
import bukkit.util.action.DimAltAction;
import bukkit.util.action.PlayTimeAction;
import bukkit.util.action.StorageAction;
import bukkit.util.console.Logger;
import common.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

public final class MainBukkit extends JavaPlugin {
    public static MainBukkit instance;
    private java.util.logging.Logger logger;
    public Mysql database;
    public String servername;
    FileConfiguration config = getConfig();
    static Map<String, Integer> worlds = new HashMap<String, Integer>();
    public static Map<String, Integer> lock = new HashMap<String, Integer>();
    public Cooldown cooldown;
    private common.Logger commonLogger;

    @Override
    public void onEnable() {
        this.instance = this;

        // Affiche le tag / version au lancement
        Logger.tag();
        PluginDescriptionFile pdf = getDescription();
        Logger.info("Chargement de la version Bukkit: " + pdf.getVersion() + " Auteur: " + pdf.getAuthors() + " Site: " + pdf.getWebsite());
        Logger.info("Lecture de la configuration...");

        this.createConfig();
        this.servername = getConfig().getString("id");

        // Check if ISOWORLDS-SAS exists
        File checkSAS = new File(ManageFiles.getPath() + "ISOWORLDS-SAS");
        if (!checkSAS.exists()) {
            checkSAS.mkdir();
            Logger.info("Dossier ISOWORLDS-SAS crée !");
        }

        File source = new File(ManageFiles.getPath());
        // Retourne la liste des isoworld tag
        for (File f : ManageFiles.getOutSAS(new File(source.getPath()))) {
            ManageFiles.deleteDir(new File(f.getPath() + "/uid.dat"));
            ManageFiles.deleteDir(new File(f.getPath() + "/session.lock"));
            // Gestion des IsoWorlds non push, on les tag à @PUSH si pas de tag @PUSHED
            if (!f.getName().contains("@PUSHED")) {
                ManageFiles.rename(ManageFiles.getPath() + "/" + f.getName(), "@PUSH");
                Logger.warning("[IsoWorlds-SAS]: IsoWorlds désormais TAG à PUSH");
            }
        }

        // Dim reset
        ResetAutoDims.reset();

        // Purge map
        worlds.clear();
        lock.clear();

        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);

        this.getCommand("iw").setExecutor(new Commands());

        this.database = new Mysql(
                getConfig().getString("sql.serveur"),
                getConfig().getInt("sql.port"),
                getConfig().getString("sql.nom-bdd"),
                getConfig().getString("sql.utilisateur"),
                getConfig().getString("sql.mdp"), true
        );

        Logger.info("Connexion à la base de données...");
        try {
            this.database.connect();
        } catch (Exception ex) {
            Logger.severe("Une erreur est survenue lors de la connexion à la base de données: " + ex.getMessage());
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.commonLogger = new common.Logger("bukkit");
        this.cooldown = new Cooldown(this.database, this.servername, "bukkit", this.commonLogger);
        Logger.info("Démarrage des tâches...");
        everyMinutes();
        Logger.info("IsoWorlds connecté avec succès à la base de données !");

        // Set global status 1
        StorageAction.setGlobalStatus(Msg.keys.SQL);

        // Gen dim ALT
        DimAltAction.generateDim();

    }

    @Override
    public void onDisable() {
        Logger.info("IsoWorlds désactivé !");
        Bukkit.getScheduler().cancelTasks(this);
        this.instance = null;
        this.servername = null;
        this.database = null;
        this.logger = null;

    }

    private void everyMinutes() {
        int x = getConfig().getInt("max-inactive-time");

        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTaskAsynchronously(MainBukkit.this.instance, () -> {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                PlayTimeAction.updatePlayTime(p, Msg.keys.SQL);
            }
        }), 0, 1200);

        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTask(MainBukkit.this.instance, () -> {
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
            for (World world : Bukkit.getServer().getWorlds()) {
                // Si le monde est chargé et contient IsoWorld
                if (world != null & world.getName().contains("-IsoWorld")) {

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
                            // Suppression dans le tableau
                            worlds.remove(world.getName());

                            // Vérification du statut du monde, si il est push ou non
                            if (!StorageAction.getStatus(world.getName(), Msg.keys.SQL)) {
                                Logger.info("debug 1");
                                File check = new File(ManageFiles.getPath() + world.getName());
                                // Si le dossier existe alors on met le statut à 1 (push)
                                if (check.exists()) {
                                    Logger.info("debug 2");

                                    // World name
                                    String wname = world.getName();

                                    // Save before unload
                                    Bukkit.getServer().getWorld(wname).save();

                                    //Kick all players
                                    for (Player p : world.getPlayers()) {
                                        Locations.teleport(p, "Isolonice");
                                    }

                                    //Unload world
                                    if (!Bukkit.getServer().unloadWorld(wname, false)) {
                                        Logger.info("Unloading of world failed");
                                    }

                                    //Set pushed status bdd
                                    StorageAction.setStatus(wname, 1, Msg.keys.SQL);

                                    // Tag du dossier en push, delayed et suppression uid.session
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + wname + "/uid.dat"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + wname + "/session.lock"));
                                    ManageFiles.rename(ManageFiles.getPath() + wname, "@PUSH");

                                    Logger.info("- " + wname + " : PUSH avec succès");
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
        }), 0, 1200);
    }

    public static MainBukkit getInstance() {
        return instance;
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                Logger.warning("Fichier de configuration non trouvé, création en cours...");
                saveDefaultConfig();
            } else {
                Logger.info("Lecture de la configuration...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
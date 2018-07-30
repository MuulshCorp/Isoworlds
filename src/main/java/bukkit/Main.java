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
package bukkit;

import bukkit.command.Commands;
import bukkit.configuration.Configuration;
import bukkit.listener.Listeners;
import bukkit.util.action.DimAltAction;
import bukkit.util.action.StorageAction;
import bukkit.util.console.Logger;
import bukkit.util.task.PlayerStatistic.PlayTime;
import bukkit.util.task.SAS.Push;
import common.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

public final class Main extends JavaPlugin implements IMain {
    public static Main instance;
    private java.util.logging.Logger logger;
    public Mysql database;
    public String servername;
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
        this.servername = getConfig().getString("Id");

        // Create needed dirs
        ManageFiles.initIsoworldsDirs();

        File source = new File(ManageFiles.getPath());
        // Retourne la liste des Isoworld tag
        for (File f : ManageFiles.getOutSAS(new File(source.getPath()))) {
            ManageFiles.deleteDir(new File(f.getPath() + "/uid.dat"));
            ManageFiles.deleteDir(new File(f.getPath() + "/session.lock"));
            // Gestion des Isoworlds non push, on les tag à @PUSH si pas de tag @PUSHED
            // Tag Isoworlds @PUSH if Storage config enabled
            if (Configuration.getStorage()) {
                if (!f.getName().contains("@PUSHED")) {
                    ManageFiles.rename(ManageFiles.getPath() + "/" + f.getName(), "@PUSH");
                    Logger.warning("[Isoworlds-SAS]: Isoworlds désormais TAG à PUSH");
                }
            }
        }

        // Register listeners
        Bukkit.getServer().getPluginManager().registerEvents(new Listeners(), this);

        this.getCommand("iw").setExecutor(new Commands());

        this.database = new Mysql(
                getConfig().getString("sql.server"),
                getConfig().getInt("sql.port"),
                getConfig().getString("sql.database"),
                getConfig().getString("sql.username"),
                getConfig().getString("sql.password"), true
        );

        Logger.info("Connexion à la base de données...");
        try {
            this.database.connect();
            Logger.info("Isoworlds connecté avec succès à la base de données !");
        } catch (Exception ex) {
            Logger.severe("Une erreur est survenue lors de la connexion à la base de données: " + ex.getMessage());
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Logger.info("Démarrage des tâches...");
        this.commonLogger = new common.Logger("bukkit");
        this.cooldown = new Cooldown(this.database, this.servername, "bukkit", this.commonLogger);

        // Log configs
        Logger.info("[CONFIG] id: " + Configuration.getId());
        Logger.info("[CONFIG] main-worldname: " + Configuration.getMainWorld());
        Logger.info("[CONFIG] main-world-spawn-coordinate: " + Configuration.getMainWorldSpawnCoordinate());
        Logger.info("[CONFIG] inactivity-before-world-unload: " + Configuration.getInactivityTime());

        Manager.instance = Main.getInstance();

        // Set structure if needed
        getMysql().setStructure();

        // ****** MODULES ******

        // DimensionAlt
        if (Configuration.getDimensionAlt()) {
            // Gen alt dims (Mining, Exploration
            DimAltAction.generateDim();
        }

        // Auto reset process alt dims (every x days) (Nether, End, Mining, Exploration)
        ResetAutoDims.reset("bukkit");

        // Storage
        if (Configuration.getStorage()) {
            // Start push action (unload task with tag)
            Push.PushProcess(Configuration.getInactivityTime());
            // Set global status 1
            StorageAction.setGlobalStatus();
        }

        // PlayTime
        if (Configuration.getPlayTime()) {
            // Start playtime task
            PlayTime.IncreasePlayTime();
        }

        // ********************

        // Copy lang.yml file
        if (ManageFiles.getLangPath() == null) {
            ManageFiles.copy(getResource("lang.yml"), new File(System.getProperty("user.dir") + "/plugins/Isoworlds/lang.yml"));
        }

        // Loading messages
        Msg.keys();
    }

    @Override
    public void onDisable() {
        Logger.info("Isoworlds désactivé !");
        Bukkit.getScheduler().cancelTasks(this);
        this.instance = null;
        this.servername = null;
        this.database = null;
        this.logger = null;

    }

    public static Main getInstance() {
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

    @Override
    public Mysql getMysql() {
        return this.database;
    }

    @Override
    public String getServername() {
        return this.servername;
    }

    @Override
    public Map<String, Integer> getLock() {
        return lock;
    }
}
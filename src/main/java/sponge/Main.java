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
package sponge;

import com.google.inject.Inject;

import common.*;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.PluginContainer;
import sponge.configuration.Configuration;
import sponge.listener.Listeners;
import sponge.util.action.DimsAltAction;
import sponge.util.action.StorageAction;
import sponge.util.console.Logger;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import sponge.command.*;
import sponge.util.task.PlayerStatistic.PlayTime;
import sponge.util.task.SAS.PreventLoadingAtStart;
import sponge.util.task.SAS.Push;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = "isoworlds",
        name = "Isoworlds",
        description = "Large scale personal world manager",
        url = "https://isolonice.fr",
        version = "1.9.4.2-hotfix-S6",
        authors = {
                "Sythiel"
        }
)

public class Main implements IMain {
    public static Main instance;
    public final common.Logger commonLogger;
    private org.slf4j.Logger logger;
    private Game game;
    public String servername;
    public static Map<String, Integer> lock = new HashMap<String, Integer>();
    public Cooldown cooldown;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File configuration = null;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader = null;
    private CommentedConfigurationNode configurationNode = null;
    public Mysql database;

    @Inject
    public PluginContainer pluginContainer;

    @Inject
    public Main(org.slf4j.Logger logger, Game game) {
        this.logger = logger;
        this.commonLogger = new common.Logger("sponge");
        this.game = game;
        instance = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {

        this.initServerName();
        this.initMySQL();

        registerEvents();
        logger.info("Chargement des Isoworlds...");

        Sponge.getCommandManager().register(this, Commands.getCommand(), "iw", "Isoworld", "Isoworlds");
        logger.info("Les Isoworlds sont chargés et opérationnels !");
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {

        // Check if Isoworlds-SAS folder exists
        File checkSAS = new File(ManageFiles.getPath() + "Isoworlds-SAS");
        if (!checkSAS.exists()) {
            checkSAS.mkdir();
            Logger.info("Dossier Isoworlds-SAS crée !");
        }

        try {
            if (!this.configuration.exists()) {
                Logger.warning("Fichier de configuration non trouvé, création en cours...");
                this.configuration.createNewFile();
                this.configurationNode = ((CommentedConfigurationNode) this.configurationLoader.load());
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Id"}).setValue("DEV").
                        setComment("Server name stored in database, feel free. Example: (AgrarianSkies2 : AS2)");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "MainWorld"}).setValue("Isolonice")
                        .setComment("Main world name (not folder name), used to teleport players on login/logout and build safe spawn (avoid death)");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "MainWorldSpawnCoordinate"}).setValue("0;60;0").
                        setComment("Default spawn position is 0,60,0");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules"}).
                        setComment("Differents modules, if enabled then adjust parameters if not (disabled) skip them");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "AutomaticUnload", "Enabled"}).setValue(true).
                        setComment("This module will unload every inactive Isoworlds for a given time (check every minutes)\n"
                                + "Once unload, plugin will add @PUSH to worlds forldername\n"
                                + "Then the storage module will push them to the backup storage defined by Isoworlds-SAS (script on github)\n"
                                + "If automatic unload is disabled, storage still works on restarts (push every Isoworlds on backup storage at start)");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "AutomaticUnload", "InactivityTime"}).setValue(15);

                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "Storage", "Enabled"}).setValue(true).
                        setComment("This module will handle backup storage (script on github), Isoworlds will be pushed at server start and worlds unload");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "DimensionAlt", "Enabled"}).setValue(true).
                        setComment("This module creates automatically alt dimensions (Mining, Exploration) (Warps access on Isoworlds menu)");

                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "DimensionAlt", "Mining"}).setValue(true);
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "DimensionAlt", "Exploration"}).setValue(true);

                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "SafePlateform", "Enabled"}).setValue(true).
                        setComment("Generate a bedrock plateform on nether/end (0,60,0 default if no Y safe position found)\n"
                                + "Clean 3*3 if filled, check at every warp action");

                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "SafeSpawn", "Enabled"}).setValue(true).
                        setComment("Generate 1*1 dirt on Isoworlds spawn if the spawn coordinate is empty (Y axis), to avoid death\n"
                                + "Breaking this dirt doesn't drop\n"
                                + "If Y axis is not empty then it will teleport the player on the highest solid position\n"
                                + "Handle lava and water");

                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "SpawnProtection", "Enabled"}).setValue(true).
                        setComment("This module disable player's interaction on main world spawn");

                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "Border", "Enabled"}).setValue(true).
                        setComment("This module define world borders");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "Border", "DefaultRadiusSize"}).setValue(250);
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "Border", "SmallRadiusSize"}).setValue(500);
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "Border", "MediumRadiusSize"}).setValue(750);
                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "Border", "LargeRadiusSize"}).setValue(1000);

                this.configurationNode.getNode(new Object[]{"Isoworlds", "Modules", "PlayTime", "Enabled"}).setValue(true).
                        setComment("This module will count playtime of players (by simply adding 1 every minutes if player is online)");

                this.configurationNode.getNode(new Object[]{"Isoworlds", "sql"}).
                        setComment("MySQL server, this configuration is needed as we don't handle sqlite atm");

                this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "host"}).setValue("IP_ADDRESS");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "port"}).setValue(3306);
                this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "database"}).setValue("DATABASE_NAME");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "username"}).setValue("DATABASE_USERNAME");
                this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "password"}).setValue("PASSWORD");
                this.configurationLoader.save(this.configurationNode);
            }

            Logger.tag();
            PluginContainer pdf = Sponge.getPluginManager().getPlugin("Isoworlds").get();
            Logger.info("Chargement de la version Sponge: " + pdf.getVersion().orElse("Non définie") + " Auteur: " + pdf.getAuthors() + " Site: " + pdf.getUrl());

            Logger.info("Lecture de la configuration...");
            this.initServerName();
            Logger.info("Connexion à la base de données...");
            if (!this.initMySQL()) {
                return;
            }
            Logger.info("Isoworlds connecté avec succès à la base de données !");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Copy lang.yml if not in config folder
        // thx @ryantheleac for intellij module path
        try {
            final Path localePath = Paths.get(configuration.getParent());
            final Asset asset = this.pluginContainer.getAsset("lang.yml").orElse(null);
            if (!new File(localePath.toString() + "/lang.yml").exists()) {
                if (asset != null) {
                    asset.copyToDirectory(localePath);
                }
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

        this.cooldown = new Cooldown(this.database, this.servername, "sponge", this.commonLogger);

        // Log configs
        Logger.info("[CONFIG] id: " + Configuration.getId());
        Logger.info("[CONFIG] main_worldname: " + Configuration.getMainWorld());
        Logger.info("[CONFIG] main_world_spawn_coordinate: " + Configuration.getMainWorldSpawnCoordinate());
        Logger.info("[CONFIG] inactivity_before_world_unload: " + Configuration.getInactivityTime());

        // Init manager
        Manager.instance = Main.instance;

        // Set structure if needed
        this.database.setStructure();
    }

    @Listener
    public void onPostInit(GameAboutToStartServerEvent event) {
        // ****** MODULES ******

        // Isoworlds-SAS move iw to folder sas
        PreventLoadingAtStart.move();
        // Reset auto atl dim process
        ResetAutoDims.reset("sponge");
        // *********************

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

        // *********************

        // Loading messages
        Msg.keys();
    }

    @Listener
    public void onGameStarted(GameStartedServerEvent event) {
        // Move iw from Isoworlds-SAS to main world folder
        PreventLoadingAtStart.moveBack();

        // ****** MODULES ******

        // DimensionAlt
        if (Configuration.getDimensionAlt()) {
            // Gen alt dim
            DimsAltAction.generateDim();
        }
        // *********************
    }

    private void registerEvents() {
        Sponge.getEventManager().registerListeners(this, new Listeners());
    }

    public Game getGame() {
        return game;
    }

    public org.slf4j.Logger getLogger() {
        return logger;
    }

    private boolean initMySQL() {
        if (this.configurationNode == null) {
            try {
                this.configurationNode = this.configurationLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.database == null) {
            this.database = new Mysql(
                    (String) this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "host"}).getValue(),
                    (Integer) this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "port"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "database"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "username"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"Isoworlds", "sql", "password"}).getValue(),
                    true
            );

            try {
                this.database.connect();
            } catch (Exception ex) {
                Logger.info("Une erreur est survenue lors de la connexion à la base de données: " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void initServerName() {
        if (this.configurationNode == null) {
            try {
                this.configurationNode = this.configurationLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.servername = (String) this.configurationNode.getNode(new Object[]{"Isoworlds", "Id"}).getValue();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
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

    public CommentedConfigurationNode getConfig() {
        return this.configurationNode;
    }
}
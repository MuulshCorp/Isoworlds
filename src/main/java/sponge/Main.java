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
package sponge;

import com.google.inject.Inject;

import common.*;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
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
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import sponge.command.*;
import sponge.util.task.PlayerStatistic.PlayTime;
import sponge.util.task.SAS.PreventLoadingAtStart;
import sponge.util.task.SAS.Push;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        id = "isoworlds",
        name = "IsoWorlds",
        description = "Large scale personal world manager",
        url = "https://isolonice.fr",
        version = "1.9.3.4-BETA",
        authors = {
                "Sythiel"
        }
)

public class Main {
    public static Main instance;
    public final common.Logger commonLogger;
    private org.slf4j.Logger logger;
    private Game game;
    public String servername;
    public static Map<String, Integer> lock = new HashMap<String, Integer>();
    public Cooldown cooldown;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configuration = null;
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader = null;
    private CommentedConfigurationNode configurationNode = null;
    public Mysql database;

    @Inject
    public Main(org.slf4j.Logger logger, Game game) {
        this.logger = logger;
        this.commonLogger = new common.Logger("sponge");
        this.game = game;
        instance = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {

        // ISOWORLDS-SAS move iw to folder sas
        PreventLoadingAtStart.move();
        // Reset auto atl dim process
        ResetAutoDims.reset("sponge");
        this.initServerName();
        this.initMySQL();
        // Set global status 1
        StorageAction.setGlobalStatus(Msg.keys.SQL);

        registerEvents();
        logger.info("Chargement des IsoWorlds...");

        Sponge.getCommandManager().register(this, Commands.getCommand(), "iw", "isoworld", "isoworlds");
        logger.info("Les IsoWorlds sont chargés et opérationnels !");
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {

        // Check if ISOWORLDS-SAS folder exists
        File checkSAS = new File(ManageFiles.getPath() + "ISOWORLDS-SAS");
        if (!checkSAS.exists()) {
            checkSAS.mkdir();
            Logger.info("Dossier ISOWORLDS-SAS crée !");
        }

        try {
            if (!this.configuration.exists()) {
                Logger.warning("Fichier de configuration non trouvé, création en cours...");
                this.configuration.createNewFile();
                this.configurationNode = ((CommentedConfigurationNode) this.configurationLoader.load());
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "id"}).setValue("SRV_NAME");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "main_worldname"}).setValue("WORLD_NAME");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "main_world_spawn_coordinates"}).setValue("0;60;0");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "inactivity_before_world_unload"}).setValue("MIN_BEFORE_UNLOAD");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_host"}).setValue("IP_ADDRESS");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_port"}).setValue(3306);
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_database"}).setValue("DATABASE_NAME");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_username"}).setValue("DATABASE_USERNAME");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_password"}).setValue("PASSWORD");
                this.configurationLoader.save(this.configurationNode);
            }

            Logger.tag();   /* Affiche le tag / version au lancement */
            PluginContainer pdf = Sponge.getPluginManager().getPlugin("Isoworlds").get();
            Logger.info("Chargement de la version Sponge: " + pdf.getVersion().orElse("Non définie") + " Auteur: " + pdf.getAuthors() + " Site: " + pdf.getUrl());

            Logger.info("Lecture de la configuration...");
            this.initServerName();
            Logger.info("Connexion à la base de données...");
            if (!this.initMySQL()) {
                return;
            }
            Logger.info("IsoWorlds connecté avec succès à la base de données !");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.cooldown = new Cooldown(this.database, this.servername, "sponge", this.commonLogger);

        // Log configs
        Logger.info("[CONFIG] id: " + this.configurationNode.getNode(new Object[]{"IsoWorlds", "id"}).getValue());
        Logger.info("[CONFIG] main_worldname: " + this.configurationNode.getNode(new Object[]{"IsoWorlds", "main_worldname"}).getValue());
        Logger.info("[CONFIG] main_world_spawn_coordinate: " + this.configurationNode.getNode(new Object[]{"IsoWorlds", "main_world_spawn_coordinates"}).getValue());
        Logger.info("[CONFIG] inactivity_before_world_unload: " + this.configurationNode.getNode(new Object[]{"IsoWorlds", "inactivity_before_world_unload"}).getValue());

        // Start push action (unload task with tag)
        Push.PushProcess((Integer) this.configurationNode.getNode(new Object[]{"IsoWorlds", "inactivity_before_world_unload"}).getValue());
        PlayTime.IncreasePlayTime();    /* Start playtime task */
    }

    @Listener
    public void onGameStarted(GameStartedServerEvent event) {
        // Move iw from ISOWORLDS-SAS to main world folder
        PreventLoadingAtStart.moveBack();
        // Gen alt dim
        DimsAltAction.generateDim();
    }

    public CommentedConfigurationNode rootNode() {
        return this.configurationNode;
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
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_host"}).getValue(),
                    (Integer) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_port"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_database"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_username"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_password"}).getValue(),
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
            this.servername = (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "id"}).getValue();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }
}
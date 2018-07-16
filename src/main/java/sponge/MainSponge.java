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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.listener.Listeners;
import sponge.util.action.DimsAltAction;
import sponge.util.action.IsoWorldsAction;
import sponge.util.action.PlayTimeAction;
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
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

public class MainSponge {
    public static MainSponge instance;
    public final common.Logger commonLogger;
    private org.slf4j.Logger logger;
    private Game game;
    public String servername;
    static Map<String, Integer> worlds = new HashMap<String, Integer>();
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
    public MainSponge(org.slf4j.Logger logger, Game game) {
        this.logger = logger;
        this.commonLogger = new common.Logger("sponge");
        this.game = game;
        instance = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        String name = "";
        // ISOWORLDS-SAS
        logger.info("[IsoWorlds-SAS]: Stockage des IsoWorlds un tag dans le SAS");
        File dest = new File(ManageFiles.getPath() + "/ISOWORLDS-SAS/");
        File source = new File(ManageFiles.getPath());
        // Retourne la liste des isoworld tag
        for (File f : ManageFiles.getOutSAS(new File(source.getPath()))) {
            ManageFiles.deleteDir(new File(f.getPath() + "/level_sponge.dat"));
            ManageFiles.deleteDir(new File(f.getPath() + "/level_sponge.dat_old"));
            // Gestion des IsoWorlds non push, si ne contient pas de tag alors "PUSH-SAS" et on le renomme lors de la sortie
            if (ManageFiles.move(source + "/" + f.getName(), dest.getPath())) {
                // Si le dossier n'est pas TAG et que le dossier de ce même nom avec TAG n'existe pas
                if (!f.getName().contains("@PUSHED")) {
                    // Si le isoworld possède pas de @PUSHED dans le dossier de base ou le SAS alors on supprime
                    if ((new File(ManageFiles.getPath() + "ISOWORLDS-SAS/" + f.getName() + "@PUSHED").exists())
                            || (new File(f.getPath() + f.getName() + "@PUSHED").exists())) {
                        ManageFiles.deleteDir(new File(ManageFiles.getPath() + "ISOWORLDS-SAS/" + f.getName()));
                        logger.info("[IsoWorlds-SAS: Anomalie sur le IsoWorld " + f.getName());
                        continue;
                    }
                    ManageFiles.rename(ManageFiles.getPath() + "ISOWORLDS-SAS/" + f.getName(), "@PUSH");
                    logger.info("[IsoWorlds-SAS]: IsoWorlds désormais TAG à PUSH");
                }
            } else {
                logger.info("[IsoWorlds-SAS]: Echec de stockage > " + name);
            }
        }

        ResetAutoDims.reset();
        this.initServerName();
        this.initMySQL();
        // Set global status 1
        StorageAction.setGlobalStatus(Msg.keys.SQL);

        // --------------

        registerEvents();
        logger.info("Chargement des IsoWorlds...");

        Sponge.getCommandManager().register(this, IsoworldsCommande.getCommand(), "iw", "isoworld", "isoworlds");
        logger.info("Les IsoWorlds sont chargés et opérationnels !");

        // Purge map
        worlds.clear();
        lock.clear();
    }

    private void everyMinutes() {
        Task task = Task.builder().execute(() -> this.unload())
                .async().delay(100, TimeUnit.MILLISECONDS).interval(1, TimeUnit.MINUTES)
                .name("Analyse des IsoWorlds vides...").submit(this.instance);
    }

    private void unload() {
        int x = 15;

        // Update playtime
        Task.builder().execute(() -> {
            for (Player p : Sponge.getServer().getOnlinePlayers()) {
                PlayTimeAction.updatePlayTime(p, Msg.keys.SQL);
            }
        }).submit(this);

        // IsoWorlds task unload
        Task.builder().execute(() -> {
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
            for (World world : Sponge.getServer().getWorlds()) {
                // Si le monde est chargé et contient IsoWorld
                if (world.isLoaded() & world.getName().contains("-IsoWorld")) {

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

                            // Sauvegarde du monde et déchargement
                            try {
                                Sponge.getServer().getWorld(world.getName()).get().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                                continue;
                            }

                            // Save & unload AUTO CORRECT
                            try {
                                world.save();
                                // If is mirrored then unload + remove then return
                                if (StorageAction.isMirrored(world.getName()) == 1) {
                                    Logger.severe("--- Anomalie détectée, unload interrompu et suppression de l'anomalie: " + world.getName() + " ---");
                                    Sponge.getServer().unloadWorld(world);
                                    Sponge.getServer().deleteWorld(world.getProperties());
                                    Logger.severe("--- Anomalie: Corrigée, suppression effectuée avec succès de l'isoworld: " + world.getName() + " ---");
                                    continue;
                                } else {
                                    if (!Sponge.getServer().unloadWorld(world)) {
                                        Logger.severe("--- Echec du déchargement de l'IsoWorld: " + world.getName() + " ---");
                                        continue;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }

                            // Suppression dans le tableau
                            worlds.remove(world.getName());

                            // Vérification du statut du monde, si il est push ou non
                            if (!StorageAction.getStatus(world.getName(), Msg.keys.SQL)) {
                                Logger.info("debug 1");
                                File check = new File(ManageFiles.getPath() + world.getName());
                                // Si le dossier existe alors on met le statut à 1 (push)
                                if (check.exists()) {
                                    Logger.info("debug 2");
                                    StorageAction.setStatus(world.getName(), 1, Msg.keys.SQL);

                                    // Suppression ID
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat_old"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/session.lock"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/forcedchunks.dat"));

                                    // Tag du dossier en push
                                    ManageFiles.rename(ManageFiles.getPath() + world.getName(), "@PUSH");
                                    Logger.info("- " + world.getName() + " : PUSH avec succès");

                                    // Suppression du monde
                                    try {
                                        if (Sponge.getServer().deleteWorld(world.getProperties()).get()) {
                                            Logger.info("- " + world.getName() + " : Isoworld supprimé avec succès !");
                                        }
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }

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
        }).submit(this);

    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {

        // Check if ISOWORLDS-SAS exists
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
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "main_spawn_coordinates"}).setValue("0;60;0");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "inactivity_world_delay"}).setValue("MINUTES_BEFORE_UNLOAD");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_host"}).setValue("IP_ADDRESS");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_port"}).setValue(3306);
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_database"}).setValue("DATABASE_NAME");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_username"}).setValue("DATABASE_USERNAME");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_password"}).setValue("PASSWORD");
                this.configurationLoader.save(this.configurationNode);
            }

            // Affiche le tag / version au lancement
            Logger.tag();
            PluginContainer pdf = Sponge.getPluginManager().getPlugin("Isoworlds").get();
            Logger.info("Chargement de la version Bukkit: " + pdf.getVersion() + " Auteur: " + pdf.getAuthors() + " Site: " + pdf.getUrl());

            Logger.info("Lecture de la configuration...");
            this.initServerName();
            Logger.info("Connexion à la base de données...");
            if (!this.initMySQL()) {
                return;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Logger.info("IsoWorlds connecté avec succès à la base de données !");
        this.cooldown = new Cooldown(this.database, this.servername, "sponge", this.commonLogger);
        everyMinutes();
    }

    @Listener
    public void onGameStarted(GameStartedServerEvent event) {
        // ISOWORLDS-SAS

        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                Logger.info("Mob Griefing protection appliqué au spawn");
                try {
                    WorldProperties worldProperties = Sponge.getServer().createWorldProperties("Isolonice", WorldArchetypes.OVERWORLD);
                    Sponge.getServer().getWorldProperties("Isolonice").get().setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
                    Sponge.getServer().saveWorldProperties(worldProperties);
                } catch (IOException io) {
                    io.printStackTrace();
                    Logger.severe("Echec application mob griefing");
                }
                Logger.info("[IsoWorlds-SAS]: Stockage des IsoWorlds un tag dans le SAS");
                File source = new File(ManageFiles.getPath() + "/ISOWORLDS-SAS/");
                File dest = new File(ManageFiles.getPath());
                // Retourne la liste des isoworld tag
                for (File f : ManageFiles.getOutSAS(new File(source.getPath()))) {
                    // Gestion des IsoWorlds non push, si ne contient pas de tag
                    if (ManageFiles.move(source + "/" + f.getName(), dest.getPath())) {
                        logger.info("[IsoWorlds-SAS]: " + f.getName() + " retiré du SAS");
                    } else {
                        logger.info("[IsoWorlds-SAS]: Echec de destockage > " + f.getName());
                    }
                }
            }
        })
                .delay(1, TimeUnit.SECONDS)
                .name("Remet les IsoWorlds hors du SAS.").submit(instance);

        // Create / load dimensions-ALT
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
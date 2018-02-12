package sponge;

import com.google.inject.Inject;

import common.Cooldown;
import common.ManageFiles;
import common.Msg;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.storage.WorldProperties;
import sponge.Listeners.IsoworldsListeners;
import sponge.Utils.IsoworldsLogger;
import sponge.Utils.IsoworldsUtils;

import common.Mysql;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import sponge.Commandes.*;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "isoworlds",
        name = "IsoWorlds",
        description = "Manager de isoworlds Isolonice",
        url = "https://isolonice.fr",
        version = "1.8.3-DEV",
        authors = {
                "Sythiel"
        }
)

public class IsoworldsSponge {
    public static IsoworldsSponge instance;
    public final common.Logger commonLogger;
    private Logger logger;
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
    public IsoworldsSponge(Logger logger, Game game) {
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
                if (!f.getName().contains("@PUSHED")) {
                    ManageFiles.rename(ManageFiles.getPath() + "/ISOWORLDS-SAS/" + f.getName(), "@PUSH");
                    logger.info("[IsoWorlds-SAS]: IsoWorlds désormais TAG à PUSH");
                }
            } else {
                logger.info("[IsoWorlds-SAS]: Echec de stockage > " + name);
            }
        }

        this.initServerName();
        this.initMySQL();
        // Set global status 1
        IsoworldsUtils.setGlobalStatus(Msg.keys.SQL);

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
        Task.builder().execute(() -> {
            // Démarrage de la procédure, on log tout les élements du map à chaque fois
            IsoworldsLogger.warning("Démarrage de l'analayse des IsoWorlds vides pour déchargement...");
            if (worlds.isEmpty()) {
                IsoworldsLogger.info("IsoWorlds inactifs à l'analyse précédente: Aucun");
            } else {
                IsoworldsLogger.info("IsoWorlds inactifs à l'analyse précédente:");
                for (Map.Entry<String, Integer> entry : worlds.entrySet()) {
                    IsoworldsLogger.info("- " + entry);
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
                            IsoworldsLogger.warning(world.getName() + " vient d'être ajouté à l'analyse");
                        } else {
                            // Sinon on incrémente
                            worlds.put(world.getName(), worlds.get(world.getName()) + 1);
                        }

                        // Si le nombre est supérieur ou = à X on unload
                        if (worlds.get(world.getName()) >= x) {
                            IsoworldsLogger.info("La valeur de: " + world.getName() + " est de " + x + " , déchargement...");
                            // Procédure de déchargement //

                            // Sauvegarde du monde et déchargement
                            try {
                                Sponge.getServer().getWorld(world.getName()).get().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                                continue;
                            }

                            Sponge.getServer().unloadWorld(world);
                            // Suppression dans le tableau
                            worlds.remove(world.getName());

                            // Vérification du statut du monde, si il est push ou non
                            if (!IsoworldsUtils.getStatus(world.getName(), Msg.keys.SQL)) {
                                IsoworldsUtils.cm("debug 1");
                                File check = new File(ManageFiles.getPath() + world.getName());
                                // Si le dossier existe alors on met le statut à 1 (push)
                                if (check.exists()) {
                                    IsoworldsUtils.cm("debug 2");
                                    IsoworldsUtils.setStatus(world.getName(), 1, Msg.keys.SQL);

                                    // ISOWORLDS-SAS
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat"));
                                    ManageFiles.deleteDir(new File(ManageFiles.getPath() + "/" + world.getName() + "/level_sponge.dat_old"));

                                    // Tag du dossier en push
                                    ManageFiles.rename(ManageFiles.getPath() + world.getName(), "@PUSH");
                                    IsoworldsLogger.info("- " + world.getName() + " : PUSH avec succès");

                                    // Suppression du monde
                                    try {
                                        if (Sponge.getServer().deleteWorld(world.getProperties()).get()) {
                                            IsoworldsLogger.info("- " + world.getName() + " : Isoworld supprimé avec succès !");
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
                        IsoworldsLogger.warning(world.getName() + " de nouveau actif, supprimé de l'analyse");
                    }
                }
            }
            if (worlds.isEmpty()) {
                IsoworldsLogger.info("Aucun IsoWorld n'est à " + x + " minutes d'inactivité...");
                IsoworldsLogger.warning("Fin de l'analyse");
            } else {
                IsoworldsLogger.info("Les IsoWorlds vides depuis " + x + " minutes viennent d'être déchargés");
                IsoworldsLogger.warning("Fin de l'analyse");
            }
        }).submit(this);

    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {

        // Check if ISOWORLDS-SAS exists
        File checkSAS = new File(ManageFiles.getPath() + "ISOWORLDS-SAS");
        if (!checkSAS.exists()) {
            checkSAS.mkdir();
            IsoworldsLogger.info("Dossier ISOWORLDS-SAS crée !");
        }

        try {
            if (!this.configuration.exists()) {
                IsoworldsLogger.warning("Fichier de configuration non trouvé, création en cours...");
                this.configuration.createNewFile();
                this.configurationNode = ((CommentedConfigurationNode) this.configurationLoader.load());
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "id"}).setValue("isoworlds");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_host"}).setValue("176.31.106.17");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_port"}).setValue(3306);
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_database"}).setValue("isoworlds");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_username"}).setValue("isoworlds");
                this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_password"}).setValue("806de245af712155c74dea135e6491d8");
                this.configurationLoader.save(this.configurationNode);
            }

            // Affiche le tag / version au lancement
            IsoworldsLogger.tag();
            PluginContainer pdf = Sponge.getPluginManager().getPlugin("Isoworlds").get();
            IsoworldsLogger.info("Chargement de la version Bukkit: " + pdf.getVersion() + " Auteur: " + pdf.getAuthors() + " Site: " + pdf.getUrl());

            IsoworldsLogger.info("Lecture de la configuration...");
            this.initServerName();
            IsoworldsLogger.info("Connexion à la base de données...");
            if (!this.initMySQL()) {
                return;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        IsoworldsLogger.info("IsoWorlds connecté avec succès à la base de données !");
        this.cooldown = new Cooldown(this.database, this.servername, "sponge", this.commonLogger);
        everyMinutes();


    }

    @Listener
    public void onGameStarted(GameStartedServerEvent event) {
        // ISOWORLDS-SAS

        Task.builder().execute(new Runnable() {
            @Override
            public void run() {
                IsoworldsLogger.info("Mob Griefing protection appliqué au spawn");
                try {
                    WorldProperties worldProperties = Sponge.getServer().createWorldProperties("Isolonice", WorldArchetypes.OVERWORLD);
                    Sponge.getServer().getWorldProperties("Isolonice").get().setGameRule(DefaultGameRules.MOB_GRIEFING, "false");
                    Sponge.getServer().saveWorldProperties(worldProperties);
                } catch (IOException io) {
                    io.printStackTrace();
                    IsoworldsLogger.severe("Echec application mob griefing");
                }
                IsoworldsLogger.info("[IsoWorlds-SAS]: Stockage des IsoWorlds un tag dans le SAS");
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

    }

    public CommentedConfigurationNode rootNode() {
        return this.configurationNode;
    }

    private void registerEvents() {
        Sponge.getEventManager().registerListeners(this, new IsoworldsListeners());
    }

    public Game getGame() {
        return game;
    }

    public Logger getLogger() {
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
                IsoworldsLogger.info("Une erreur est survenue lors de la connexion à la base de données: " + ex.getMessage());
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
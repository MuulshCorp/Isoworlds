package sponge;

import com.google.inject.Inject;

import common.ManageFiles;
import org.spongepowered.api.world.Chunk;
import sponge.Listeners.IsoworldsListeners;
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
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "isoworlds",
        name = "IsoWorlds",
        description = "Manager de isoworlds Isolonice",
        url = "https://isolonice.fr",
        version = "1.1-SNAPSHOT",
        authors = {
                "Sythiel"
        }
)

public class IsoworldsSponge {

    public static IsoworldsSponge instance;
    private Logger logger;
    private Game game;
    public String servername;
    static Map<String, Integer> worlds = new HashMap<String, Integer>();

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
        this.game = game;
        instance = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {

        registerEvents();

        logger.info("Chargement des IsoWorlds...");

        Sponge.getCommandManager().register(this, IsoworldsCommande.getCommand(), "iw", "isoworld", "isoworlds");

        logger.info("Les IsoWorlds sont chargés et opérationnels !");
    }

    private void everyMinutes() {
        Task task = Task.builder().execute(() -> this.unload())
                .async().delay(100, TimeUnit.MILLISECONDS).interval(1, TimeUnit.MINUTES)
                .name("Analyse des IsoWorlds vides...").submit(this.instance);
    }

    private void unload() {
        Task.builder().execute(() -> {
            IsoworldsUtils.cm("[IsoWorlds] Analyse des IsoWorls vides...");
            IsoworldsUtils.cm("map: " + worlds);
            for (World world : Sponge.getServer().getWorlds()) {
                // Si chargé et pas présent dans map et vide
                if (worlds.get(world.getName()) == null & world.getPlayers().size() == 0) {
                    if (world.getName().contains("-IsoWorld")) {
                        worlds.put(world.getName(), 1);
                    } else {
                        continue;
                    }
                    // Si tableau rempli
                } else if (worlds.get(world.getName()) != null) {
                    // Si de nouveau des joueurs on remove
                    if (world.getPlayers().size() != 0) {
                        worlds.remove(world.getName());
                        continue;
                        // Sinon on incrémente et on check si c'est à 10
                    } else {
                        worlds.put(world.getName(), worlds.get(world.getName()) + 1);
                        if (world.getPlayers().size() == 0 & worlds.get(world.getName()) == 5) {
                            IsoworldsUtils.cm("La valeur de: " + world.getName() + " est de 5 ! On unload !");
                            Sponge.getServer().unloadWorld(world);
                            worlds.remove(world.getName());

                            // Prepair for pushing to backup server
                            if (ManageFiles.rename(ManageFiles.getPath() + world.getName(), world.getName() + "@PUSH")) {
                                IsoworldsUtils.cm("PUSH OK");
                            } else {
                                IsoworldsUtils.cm("PUSH ERREUR");
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
            IsoworldsUtils.cm("[IsoWorlds] Les IsoWorlds vides depuis 5 minutes viennent d'être déchargé");


        }).name("Les IsoWorlds vides depuis 5 minutes viennent d'être déchargé").submit(this);
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        try {
            if (!this.configuration.exists()) {
                this.logger.info("Fichier de configuration non trouvé, création en cours...");
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

            this.logger.info("Lecture de la configuration   ...");
            this.configurationNode = ((CommentedConfigurationNode) this.configurationLoader.load());
            try {
                this.servername = (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "id"}).getValue();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            this.database = new Mysql(
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_host"}).getValue(),
                    (Integer) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_port"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_database"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_username"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"IsoWorlds", "sql_password"}).getValue(),
                    true
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        this.logger.info("Connexion à la base de données...");
        try {
            this.database.connect();
        } catch (Exception ex) {
            logger.info("Une erreur est survenue lors de la connexion à la base de données: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }
        this.logger.info("IsoWorlds connecté avec succès à la base de données !");
        everyMinutes();



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
}
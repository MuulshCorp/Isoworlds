package sponge;

import com.google.inject.Inject;

import sponge.Listeners.IworldsListeners;
import sponge.Utils.IworldsUtils;

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
        id = "iworlds",
        name = "iWorlds",
        description = "Manager de iworlds Isolonice",
        url = "https://isolonice.fr",
        version = "0.6-DEV",
        authors = {
                "Sythiel"
        }
)

public class IworldsSponge {

    public static IworldsSponge instance;
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
    public IworldsSponge(Logger logger, Game game) {
        this.logger = logger;
        this.game = game;
        instance = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {

        registerEvents();

        logger.info("Chargement des iWorlds...");

        Sponge.getCommandManager().register(this, IworldsCommande.getCommand(), "iw", "iworld", "iworlds");

        logger.info("Les iWorlds sont chargés et opérationnels !");
    }

    private void everyMinutes() {
        Task task = Task.builder().execute(() -> this.unload())
                .async().delay(100, TimeUnit.MILLISECONDS).interval(1, TimeUnit.MINUTES)
                .name("Analyse des iWorlds vides...").submit(this.instance);
    }

    private void unload() {
        Task.builder().execute(() -> {
            IworldsUtils.cm("[iWorlds] Analyse des iWorls vides...");
            for(World world : Sponge.getServer().getWorlds()) {
                if (world.isLoaded() & worlds.get(world.getName()) == null & world.getPlayers().size() == 0) {
                    if (world.getName().contains("-iWorld")) {
                        worlds.put(world.getName(), 1);
                    } else {
                        continue;
                    }
                } else {
                    if (world.getPlayers().size() == 1) {
                        worlds.remove(world.getName());
                        continue;
                    }
                    worlds.put(world.getName(), worlds.get(world.getName()) + 1);
                    if (world.getPlayers().size() == 0 & worlds.get(world.getName()) == 10) {
                        IworldsUtils.cm("La valeur de: " + world.getName() + " est de 10 ! On unload !");
                        Sponge.getServer().unloadWorld(world);
                        worlds.remove(world.getName());
                    } else {
                        continue;
                    }
                }
            }
            IworldsUtils.cm("[iWorlds] Les iWorlds vides depuis 10 minutes viennent d'être déchargé");


        }).async().name("Les iWorlds vides depuis 5 minutes viennent d'être déchargé").submit(this);
    }

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        try {
            if (!this.configuration.exists()) {
                this.logger.info("Fichier de configuration non trouvé, création en cours...");
                this.configuration.createNewFile();
                this.configurationNode = ((CommentedConfigurationNode) this.configurationLoader.load());
                this.configurationNode.getNode(new Object[]{"iWorlds", "id"}).setValue("iworlds");
                this.configurationNode.getNode(new Object[]{"iWorlds", "sql_host"}).setValue("176.31.106.17");
                this.configurationNode.getNode(new Object[]{"iWorlds", "sql_port"}).setValue(3306);
                this.configurationNode.getNode(new Object[]{"iWorlds", "sql_database"}).setValue("iworlds");
                this.configurationNode.getNode(new Object[]{"iWorlds", "sql_username"}).setValue("iworlds");
                this.configurationNode.getNode(new Object[]{"iWorlds", "sql_password"}).setValue("806de245af712155c74dea135e6491d8");
                this.configurationLoader.save(this.configurationNode);
            }

            this.logger.info("Lecture de la configuration   ...");
            this.configurationNode = ((CommentedConfigurationNode) this.configurationLoader.load());
            try {
                this.servername = (String) this.configurationNode.getNode(new Object[]{"iWorlds", "id"}).getValue();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            this.database = new Mysql(
                    (String) this.configurationNode.getNode(new Object[]{"iWorlds", "sql_host"}).getValue(),
                    (Integer) this.configurationNode.getNode(new Object[]{"iWorlds", "sql_port"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"iWorlds", "sql_database"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"iWorlds", "sql_username"}).getValue(),
                    (String) this.configurationNode.getNode(new Object[]{"iWorlds", "sql_password"}).getValue(),
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
        this.logger.info("iWorlds connecté avec succès à la base de données !");
        everyMinutes();

    }

    public CommentedConfigurationNode rootNode() {
        return this.configurationNode;
    }

    private void registerEvents() {
        Sponge.getEventManager().registerListeners(this, new IworldsListeners());
    }

    public Game getGame() {
        return game;
    }

    public Logger getLogger() {
        return logger;
    }
}
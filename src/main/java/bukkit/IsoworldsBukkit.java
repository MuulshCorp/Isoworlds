package bukkit;

import bukkit.Commandes.IsoworldsCommandes;
import bukkit.Listeners.IsoworldsListeners;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.ManageFiles;
import common.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import common.Mysql;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class IsoworldsBukkit extends JavaPlugin {
    public static IsoworldsBukkit instance;
    private Logger logger;
    public Mysql database;
    public String servername;
    FileConfiguration config = getConfig();
    static Map<String, Integer> worlds = new HashMap<String, Integer>();
    public static Map<String, Integer> lock = new HashMap<String, Integer>();
    public Cooldown cooldown;
    public common.Logger commonLogger;

    @Override
    public void onEnable() {
        this.instance = this;
        this.logger = getLogger();
        this.logger.info("Lecture de la configuration...");
        this.createConfig();
        this.servername = getConfig().getString("id");

        // Purge map
        worlds.clear();
        lock.clear();

        Bukkit.getServer().getPluginManager().registerEvents(new IsoworldsListeners(), this);

        this.getCommand("iw").setExecutor(new IsoworldsCommandes());

        this.database = new Mysql(
                getConfig().getString("sql.serveur"),
                getConfig().getInt("sql.port"),
                getConfig().getString("sql.nom-bdd"),
                getConfig().getString("sql.utilisateur"),
                getConfig().getString("sql.mdp"), true
        );

        this.logger.info(this.database.toString());

        this.logger.info("Connexion à la base de données    ...");
        try {
            this.database.connect();
        } catch (Exception ex) {
            this.logger.severe("Une erreur est survenue lors de la connexion à la base de données: " + ex.getMessage());
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


        this.commonLogger = new common.Logger("bukkit");
        this.cooldown = new Cooldown(this.database, this.servername, "bukkit", this.commonLogger);
        this.logger.info("Démarrage des tâches...");
        everyMinutes();
        this.logger.info("IsoWorlds activé !");

    }

    @Override
    public void onDisable() {
        this.logger.info("IsoWorlds désactivé !");
        Bukkit.getScheduler().cancelTasks(this);
        this.instance = null;
        this.servername = null;
        this.database = null;
        this.logger = null;

    }

    private void everyMinutes() {
        int x = 15;
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTaskAsynchronously(IsoworldsBukkit.this.instance, () -> {
            IsoworldsUtils.cm("[IsoWorlds] Analyse des IsoWorls vides...");
            IsoworldsUtils.cm("map: " + worlds);
            // Boucle de tous les mondes
            for (World world : Bukkit.getServer().getWorlds()) {
                // Si le monde est chargé et contient IsoWorld
                if (world != null & world.getName().contains("-IsoWorld")) {

                    // Si le nombre de joueurs == 0
                    if (world.getPlayers().size() == 0) {
                        // Si le monde n'est pas présent dans le tableau
                        if (worlds.get(world.getName()) == null) {
                            worlds.put(world.getName(), 1);
                        } else {
                            // Sinon on incrémente
                            worlds.put(world.getName(), worlds.get(world.getName()) + 1);
                        }

                        // Si le nombre est supérieur ou = à X on unload
                        if (worlds.get(world.getName()) >= x) {
                            IsoworldsUtils.cm("La valeur de: " + world.getName() + " est de " + x + " , déchargement...");
                            // Procédure de déchargement //
                            // Sauvegarde du monde
                            if (!Bukkit.getServer().getWorld(world.getName()).equals(null)) {
                                Bukkit.getServer().unloadWorld(world.getName(), true);
                            }
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

                                    // Tag du dossier en push
                                    ManageFiles.rename(ManageFiles.getPath() + world.getName(), "@PUSH");
                                    IsoworldsUtils.cm("PUSH OK");
                                }
                            } else {
                                // Sinon on continue la boucle
                                continue;
                            }

                        }
                        // Si le nombre de joueur est supérieur à 0, purge le tableau du IsoWorld
                    } else if (worlds.get(world.getName()) != null) {
                        worlds.remove(world.getName());
                    }
                }
            }
            IsoworldsUtils.cm("[IsoWorlds] Les IsoWorlds vides depuis " + x + " minutes viennent d'être déchargé");
        }), 1200 * 1, 1200 * 1);
    }

    public static IsoworldsBukkit getInstance() {
        return instance;
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("config.yml non trouvé, création!");
                saveDefaultConfig();
            } else {
                getLogger().info("config.yml trouvé!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

package bukkit;

import bukkit.Commandes.IsoworldsCommandes;
import bukkit.Listeners.IsoworldsListeners;
import bukkit.Utils.IsoworldsUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import common.Mysql;

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

    @Override
    public void onEnable() {
        this.instance = this;
        this.logger = getLogger();
        this.logger.info("Lecture de la configuration...");
        this.createConfig();
        this.servername = getConfig().getString("id");

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
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTaskAsynchronously(IsoworldsBukkit.this.instance, () -> {
            IsoworldsUtils.cm("[IsoWorlds] Analyse des IsoWorls vides...");
            IsoworldsUtils.cm("map: " + worlds);
            for (World world : Bukkit.getServer().getWorlds()) {
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
                        worlds.put(world.getName(), worlds.get(world.getName().toString()) + 1);
                        if (world.getPlayers().size() == 0 & worlds.get(world.getName().toString()) == 5) {
                            IsoworldsUtils.cm("La valeur de: " + world.getName() + " est de 5 ! On unload !");
                            Bukkit.getServer().unloadWorld(world, true);
                            worlds.remove(world.getName());
                            // Prepair for pushing to backup server
                            //if (ManageFiles.rename(ManageFiles.getPath() + world.getName(), "@PUSH")) {
                            //    IsoworldsUtils.cm("PUSH OK");
                            //} else {
                            //    IsoworldsUtils.cm("PUSH ERREUR");
                            //}
                        } else {
                            continue;
                        }
                    }
                }
            }
            IsoworldsUtils.cm("[IsoWorlds] Les IsoWorlds vides depuis 5 minutes viennent d'être déchargé");
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

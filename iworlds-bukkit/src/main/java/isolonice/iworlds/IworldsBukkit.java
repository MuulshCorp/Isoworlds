package isolonice.iworlds;

import isolonice.iworlds.Utils.IworldsUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import commonUtils.Mysql;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class IworldsBukkit extends JavaPlugin {
    public IworldsBukkit instance;
    private Logger logger;
    public Mysql database;
    private String server;
    private int delay;
    static Map<String, Integer> worlds = new HashMap<String, Integer>();

    @Override
    public void onEnable() {
        this.instance = this;
        this.logger = getLogger();
        this.logger.info("Reading config...");
        this.createConfig();
        this.server = getConfig().getString("server");
        this.delay = getConfig().getInt("delay");

        this.database = new Mysql(
                getConfig().getString("sql.host"),
                getConfig().getInt("sql.port"),
                getConfig().getString("sql.database"),
                getConfig().getString("sql.username"),
                getConfig().getString("sql.password"), true
        );

        this.logger.info("Connecting to database...");
        try {
            this.database.connect();
        } catch (Exception ex) {
            this.logger.severe("Une erreur est survenue lors de la connexion à la base de donnéesw: " + ex.getMessage());
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.logger.info("Démarrage des tâches...");
        everyMinutes();
        this.logger.info("iWorlds activé !");
    }

    @Override
    public void onDisable() {
        this.logger.info("iWorlds désactivé !");
        Bukkit.getScheduler().cancelTasks(this);

    }

    @SuppressWarnings("deprecation")
    private void everyMinutes() {
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTaskAsynchronously(IworldsBukkit.this.instance, () -> {
            IworldsUtils.cm("[iWorlds] Analyse des iWorls vides...");
            IworldsUtils.cm("map: " + worlds);
            for (World world : Bukkit.getServer().getWorlds()) {
                if (world != null & worlds.get(world.getName()) == null & world.getPlayers().size() == 0) {
                    if (world.getName().contains("-iWorld")) {
                        IworldsUtils.cm("Monde non enregistré: " + world.getName());
                        worlds.put(world.getName(), 1);
                        IworldsUtils.cm("Valeur définie à: " + worlds.get(world.getName()));
                    } else {
                        IworldsUtils.cm("Le monde " + world.getName() + " n'est pas un iWorld");
                        continue;
                    }
                } else {
                    if (world.getPlayers().size() == 1) {
                        worlds.remove(world.getName());
                        continue;
                    }
                    IworldsUtils.cm("Monde enregistré: " + world.getName());
                    IworldsUtils.cm("Ancienne valeur: " + worlds.get(world.getName()));
                    worlds.put(world.getName(), worlds.get(world.getName()) + 1);
                    IworldsUtils.cm("Nouvelle valeur: " + worlds.get(world.getName()));
                    if (world.getPlayers().size() == 0 & worlds.get(world.getName()) == 10) {
                        IworldsUtils.cm("La valeur de: " + world.getName() + " est de 10 ! On unload !");
                        Bukkit.getServer().unloadWorld(world, true);
                        worlds.remove(world.getName());
                    } else {
                        continue;
                    }
                }
            }
            IworldsUtils.cm("[iWorlds] Les iWorlds vides depuis 10 minutes viennent d'être déchargé");
        }), 1200 * this.delay, 1200 * this.delay);
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
                getLogger().info("config.yml trouvé, création!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

package bukkit;

import bukkit.Commandes.IsoworldsCommandes;
import bukkit.Listeners.IsoworldsListeners;
import bukkit.Locations.IsoworldsLocations;
import bukkit.Utils.IsoworldsLogger;
import bukkit.Utils.IsoworldsUtils;
import common.Cooldown;
import common.ManageFiles;
import common.Msg;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import common.Mysql;
import org.bukkit.scheduler.BukkitTask;

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

        // Affiche le tag / version au lancement
        IsoworldsLogger.tag();
        PluginDescriptionFile pdf = getDescription();
        IsoworldsLogger.info("Chargement de la version Bukkit: " + pdf.getVersion() + " Auteur: " + pdf.getAuthors() + " Site: " + pdf.getWebsite());
        IsoworldsLogger.info("Lecture de la configuration...");

        this.createConfig();
        this.servername = getConfig().getString("id");

        // Check if ISOWORLDS-SAS exists
        File checkSAS = new File(ManageFiles.getPath() + "ISOWORLDS-SAS");
        if (!checkSAS.exists()) {
            checkSAS.mkdir();
            IsoworldsLogger.info("Dossier ISOWORLDS-SAS crée !");
        }

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

        IsoworldsLogger.info("Connexion à la base de données...");
        try {
            this.database.connect();
        } catch (Exception ex) {
            IsoworldsLogger.severe("Une erreur est survenue lors de la connexion à la base de données: " + ex.getMessage());
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.commonLogger = new common.Logger("bukkit");
        this.cooldown = new Cooldown(this.database, this.servername, "bukkit", this.commonLogger);
        IsoworldsLogger.info("Démarrage des tâches...");
        everyMinutes();
        IsoworldsLogger.info("IsoWorlds connecté avec succès à la base de données !");

    }

    @Override
    public void onDisable() {
        IsoworldsLogger.info("IsoWorlds désactivé !");
        Bukkit.getScheduler().cancelTasks(this);
        this.instance = null;
        this.servername = null;
        this.database = null;
        this.logger = null;

    }

    private void everyMinutes() {
        int x = 15;
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getScheduler().runTaskAsynchronously(IsoworldsBukkit.this.instance, () -> {
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
            for (World world : Bukkit.getServer().getWorlds()) {
                // Si le monde est chargé et contient IsoWorld
                if (world != null & world.getName().contains("-IsoWorld")) {

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

                            for (Player p : Bukkit.getServer().getWorld(world.getName()).getPlayers()) {
                                IsoworldsLocations.teleport(p, "Isolonice");
                            }

                            // Sauvegarde du monde et déchargement
                            if (!Bukkit.getServer().getWorld(world.getName()).equals(null)) {
                                Bukkit.getServer().unloadWorld(world, true);
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

                                    // Tag du dossier en push, delayed
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                                            ManageFiles.rename(ManageFiles.getPath() + world.getName(), "@PUSH");
                                            IsoworldsLogger.info("- " + world.getName() + " : PUSH avec succès");
                                    }, 20L);

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
        }), 600 * 1, 1200 * 1);
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
                IsoworldsLogger.warning("Fichier de configuration non trouvé, création en cours...");
                saveDefaultConfig();
            } else {
                IsoworldsLogger.info("Lecture de la configuration...");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

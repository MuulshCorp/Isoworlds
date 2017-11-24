package bukkit.Utils;

/**
 * Created by Edwin on 23/11/2017.
 */

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import static bukkit.IsoworldsBukkit.instance;


public class IsoWorldsInventory implements Listener {

    private static String name;
    private static int size;
    private static OptionClickEventHandler handler;
    private static Plugin plugin;

    private static String[] optionNames;
    private static ItemStack[] optionIcons;

    public IsoWorldsInventory(String name, int size, OptionClickEventHandler handler, Plugin plugin) {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionIcons = new ItemStack[size];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public IsoWorldsInventory setOption(int position, ItemStack icon, String name, String... info) {
        optionNames[position] = name;
        optionIcons[position] = setItemNameAndLore(icon, name, info);
        return this;
    }

    public static void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(name)) {
            IsoworldsUtils.cm("PLAYER MENU 1:" + event.getWhoClicked().getName());
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null) {
                Plugin plugin = this.plugin;
                OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, optionNames[slot]);
                handler.onOptionClick(e);
                if (e.willClose()) {
                    final Player p = (Player) event.getWhoClicked();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            p.closeInventory();
                            IsoworldsUtils.cm("PLAYER MENU 2: " + clicked.getItemMeta().getDisplayName());

                            // MENU PRINCIPAL //
                            // BIOME
                            if (clicked.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Biome")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuBiome().open(p);
                                // CONFIANCE
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Confiance")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuConfiance().open(p);
                                // CONSTRUCTION
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Construction")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuConstruction().open(p);
                                // MAISON
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Maison")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuMaison().open(p);
                                // METEO
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Météo")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuMeteo().open(p);
                                // ACTIVATION
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.RED + "Activation")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuActivation().open(p);
                                // TELEPORTATION
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Téléportation")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuTeleportation().open(p);
                            } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.RED + "Menu principal")) {
                                IsoworldsUtils.cm("PLAYER MENU 3");
                                getMenuPrincipal().open(p);
                            }

                            // SOUS-MENU //
                            // BIOMES
                            else if (event.getInventory().getName().contains("Biome")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Plaines")) {
                                    p.performCommand("iw biome plaines");
                                } else if (clicked.getItemMeta().getDisplayName().contains("Désert")) {
                                    p.performCommand("iw biome desert");
                                }
                                // CONFIANCE
                            } else if (event.getInventory().getName().contains("Confiance")) {

                                // CONSTRUCTION
                            } else if (event.getInventory().getName().contains("Construction")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Création")) {
                                    p.performCommand("iw c");
                                } else if (clicked.getItemMeta().getDisplayName().contains("Refonte")) {
                                    p.performCommand("iw r");
                                }
                                // MAISON
                            } else if (event.getInventory().getName().contains("Maison")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Maison")) {
                                    p.performCommand("iw h");
                                }
                                // METEO
                            } else if (event.getInventory().getName().contains("Météo")) {

                                //type
                                String mtype = "";
                                if (clicked.getItemMeta().getDisplayName().contains("Soleil")) {
                                    mtype = "soleil";
                                } else if (clicked.getItemMeta().getDisplayName().contains("Pluie")) {
                                    mtype = "pluie";
                                }
                                //temps
                                // 10 MINUTES
                                if (clicked.getItemMeta().getDisplayName().contains("10 minutes")) {
                                    p.performCommand("iw meteo " + mtype + " 12000");
                                    // 30 MINUTES
                                } else if (clicked.getItemMeta().getDisplayName().contains("30 minutes")) {
                                    p.performCommand("iw meteo " + mtype + " 36000");
                                    // 1 HEURE
                                } else if (clicked.getItemMeta().getDisplayName().contains("1 heure")) {
                                    p.performCommand("iw meteo " + mtype + " 72000");
                                }
                                // ACTIVATION
                            } else if (event.getInventory().getName().contains("Activation")) {
                                if (clicked.getItemMeta().getDisplayName().contains("Activer")) {
                                    p.performCommand("iw on");
                                } else if (clicked.getItemMeta().getDisplayName().contains("Désactiver")) {
                                    p.performCommand("iw off");
                                }
                                // TELEPORTATION
                            } else if (event.getInventory().getName().contains("Téléportation")) {
                                if (clicked.getItemMeta().getDisplayName().contains("-IsoWorld")) {
                                    p.performCommand("iw teleport " + clicked.getItemMeta().getDisplayName());
                                }
                            }


                        }
                    }, 1);
                }
                if (e.willDestroy()) {
                    destroy();
                }
            }
        }
    }

    public interface OptionClickEventHandler {
        public void onOptionClick(OptionClickEvent event);
    }

    public class OptionClickEvent {
        private Player player;
        private int position;
        private String name;
        private boolean close;
        private boolean destroy;

        public OptionClickEvent(Player player, int position, String name) {
            this.player = player;
            this.position = position;
            this.name = name;
            this.close = true;
            this.destroy = true;
        }

        public Player getPlayer() {
            return player;
        }

        public int getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        public boolean willClose() {
            return close;
        }

        public boolean willDestroy() {
            return destroy;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }

    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }

    //MENU PRINCIPAL
    public static IsoWorldsInventory getMenuPrincipal() {
        IsoWorldsInventory menuPrincipal = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.GOLD + "IsoWorlds: Menu principal"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Biome", "Gérez le biome des vos chunks")
                .setOption(1, new ItemStack(Material.EMERALD, 1), ChatColor.GREEN + "Confiance", "Gérez qui peut avoir accès à votre IsoWorld")
                .setOption(2, new ItemStack(Material.BRICK, 1), ChatColor.GRAY + "Construction", "Créez ou refondez votre IsoWorld")
                .setOption(3, new ItemStack(Material.BED, 1), ChatColor.BLUE + "Maison", "Rendez-vous sur votre IsoWorld")
                .setOption(4, new ItemStack(Material.DOUBLE_PLANT, 1), ChatColor.YELLOW + "Météo", "Gérez la pluie et le beau temps de votre IsoWorld")
                .setOption(5, new ItemStack(Material.LEVER, 1), ChatColor.RED + "Activation", "Chargez-Déchargez votre IsoWorld")
                .setOption(6, new ItemStack(Material.DIAMOND_BOOTS, 1), ChatColor.LIGHT_PURPLE + "Téléportation", "Téléportez vous sur un IsoWorld [STAFF]");
        return menuPrincipal;
    }

    // BIOME
    public static IsoWorldsInventory getMenuBiome() {
        IsoWorldsInventory menuBiome = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Biome"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Plaines", "Un biome relativement plat avec des collines || vallonnées et une grande quantité de fleurs")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.")

                // RETOUR
                .setOption(8, new ItemStack(Material.SAND, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuBiome;
    }

    // CONFIANCE
    public static IsoWorldsInventory getMenuConfiance() {
        IsoWorldsInventory menuConfiance = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Confiance"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GREEN + "Ajouter", "Autoriser l'accès à votre IsoWorld.")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.RED + "Retirer", "Retirer l'accès à votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.SAND, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuConfiance;
    }

    // CONSTRUCTION
    public static IsoWorldsInventory getMenuConstruction() {
        IsoWorldsInventory menuConstruction = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Construction"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Création", "Créer votre IsoWorld.")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.GREEN + "Refonte", "Réinitialiser votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.SAND, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuConstruction;
    }

    // MAISON
    public static IsoWorldsInventory getMenuMaison() {
        IsoWorldsInventory menuMaison = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Maison"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Maison", "Vous rendre sur votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.SAND, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuMaison;
    }

    // METEO
    public static IsoWorldsInventory getMenuMeteo() {
        IsoWorldsInventory menuMeteo = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Météo"), 18, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.YELLOW + "Soleil [10 minutes]", "Le temps devient paisible et ensoleillé.")
                .setOption(1, new ItemStack(Material.GRASS, 1), ChatColor.YELLOW + "Soleil [30 minutes]", "Le temps devient paisible et ensoleillé.")
                .setOption(2, new ItemStack(Material.GRASS, 1), ChatColor.YELLOW + "Soleil [1 heure]", "Le temps devient paisible et ensoleillé.")

                .setOption(9, new ItemStack(Material.SAND, 1), ChatColor.BLUE + "Pluie [10 minutes]", "Vos terres boivent l'eau de pluie.")
                .setOption(10, new ItemStack(Material.SAND, 1), ChatColor.BLUE + "Pluie [30 minutes]", "Vos terres boivent l'eau de pluie.")
                .setOption(11, new ItemStack(Material.SAND, 1), ChatColor.BLUE + "Pluie [1 heure]", "Vos terres boivent l'eau de pluie.")

                // RETOUR
                .setOption(17, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuMeteo;
    }

    // ACTIVATION
    public static IsoWorldsInventory getMenuActivation() {
        IsoWorldsInventory menuActivation = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Activation"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance)
                .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GREEN + "Activer", "Charge votre IsoWorld.")
                .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.RED + "Désactiver", "Décharge votre IsoWorld.")

                // RETOUR
                .setOption(8, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");
        return menuActivation;
    }

    // TELEPORTATION
    public static IsoWorldsInventory getMenuTeleportation() {
        IsoWorldsInventory menuTeleportation = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Téléportation"), 27, new IsoWorldsInventory.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
                event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
                event.setWillClose(true);
            }
        }, instance);
        // Boucle pour afficher le nom réel + nom pseudo du monde
        int i = 0;
        for (World w : Bukkit.getWorlds()) {
            if (w.getName().contains("-IsoWorld")) {
                String[] split = w.getName().split("-IsoWorld");
                UUID uuid = UUID.fromString(split[0]);
                String name = Bukkit.getServer().getOfflinePlayer(uuid).getName();
                menuTeleportation.setOption(i, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + w.getName(), name);
                i++;
            }
        }

        // RETOUR
        menuTeleportation.setOption(26, new ItemStack(Material.GOLD_BLOCK, 1), ChatColor.RED + "Menu principal", "Retour au menu principal");

        return menuTeleportation;
    }

}
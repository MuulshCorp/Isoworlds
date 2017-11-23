package bukkit.Utils;

/**
 * Created by Edwin on 23/11/2017.
 */

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class IsoWorldsInventory implements Listener {

    private String name;
    private int size;
    private OptionClickEventHandler handler;
    private Plugin plugin;

    private String[] optionNames;
    private ItemStack[] optionIcons;

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

    public void open(Player player) {
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
            event.setCancelled(true);
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
            this.destroy = false;
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

}

//    // Inventaire principal, affiche toutes les commandes disponibles
//    public static Inventory mainInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds -");
//    // The first parameter, is the inventory owner. I make it null to let everyone use it.
//    //The second parameter, is the slots in a inventory. Must be a multiple of 9. Can be up to 54.
//    //The third parameter, is the inventory name. This will accept chat colors.
//
//    static {
//        createDisplay(Material.CLAY, mainInventory, 0, ChatColor.GOLD + "Biome", "Gérez le biome des vos chunks", (byte) 1);
//        createDisplay(Material.CLAY, mainInventory, 1, ChatColor.GREEN + "Confiance", "Gérez qui peut avoir accès à votre IsoWorld", (byte) 13);
//        createDisplay(Material.CLAY, mainInventory, 2, ChatColor.GRAY + "Création/Refonte", "Créez, reformez votre IsoWorld", (byte) 7);
//        createDisplay(Material.CLAY, mainInventory, 3, ChatColor.BLUE + "Maison", "Rendez-vous sur votre IsoWorld", (byte) 11);
//        createDisplay(Material.CLAY, mainInventory, 3, ChatColor.YELLOW + "Météo", "Gérez la pluie et le beau temps de votre IsoWorld", (byte) 4);
//        createDisplay(Material.CLAY, mainInventory, 4, ChatColor.RED + "Activation", "Chargez-Déchargez votre IsoWorld", (byte) 14);
//        createDisplay(Material.CLAY, mainInventory, 5, ChatColor.LIGHT_PURPLE + "Téléportation", "Téléportez vous sur un IsoWorld [STAFF]", (byte) 10);
//        createDisplay(Material.CLAY, mainInventory, 6, "Temps", "Gérez la le temps de votre IsoWorld", (byte) 0);
//        //The first parameter, is the slot that is assigned to. Starts counting at 0
//    }
//
//    /*// BIOME
//    public static Inventory biomeInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + ChatColor.GOLD + "Biome"); {
//
//        createDisplay(Material.DIRT, biomeInventory, 0, ChatColor.GOLD + "Biome", "Gérez le biome des vos chunks", (byte) 1);
//    }
//
//    // CONFIANCE
//    public static Inventory confianceInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + ChatColor.GREEN + "Confiance");
//
//    static {
//
//        createDisplay(Material.DIRT, confianceInventory, 0, ChatColor.GOLD + "Confiance", "Gérez le biome des vos chunks", (byte) 13);
//    }*/
//
//    // CREATION/REFONTE
//    public static Inventory creationInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + ChatColor.GRAY + "Creation");
//
//    static {
//
//        createDisplay(Material.DIRT, creationInventory, 0, ChatColor.GOLD + "Creation", "Gérez le biome des vos chunks", (byte) 7);
//    }/*
//
//    // MAISON
//    public static Inventory maisonInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + ChatColor.BLUE + "Maison");
//
//    static {
//
//        createDisplay(Material.DIRT, maisonInventory, 0, ChatColor.GOLD + "Maison", "Gérez le biome des vos chunks", (byte) 11);
//    }
//
//    // METEO
//    public static Inventory meteoInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + "Météo");
//
//    static {
//
//        createDisplay(Material.DIRT, meteoInventory, 0, ChatColor.GOLD + "Météo", "Gérez le biome des vos chunks", (byte) 11);
//    }
//
//    // ACTIVATION
//    public static Inventory activationInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + ChatColor.RED + "Activation");
//
//    static {
//
//        createDisplay(Material.DIRT, activationInventory, 0, ChatColor.GOLD + "Activation", "Gérez le biome des vos chunks", (byte) 14);
//    }
//
//    // TELEPORTATION
//    public static Inventory teleportationInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + ChatColor.LIGHT_PURPLE + "Téléporation");
//
//    static {
//
//        createDisplay(Material.DIRT, teleportationInventory, 0, ChatColor.GOLD + "Téléporation", "Gérez le biome des vos chunks", (byte) 10);
//    }
//
//    // TEMPS
//    public static Inventory tempsInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "- IsoWorlds: " + ChatColor.YELLOW + "Temps");
//
//    static {
//
//        createDisplay(Material.DIRT, tempsInventory, 0, ChatColor.GOLD + "Temps", "Gérez le biome des vos chunks", (byte) 4);
//    }*/
//
//    // Permet de récupérer les informations dans les invententaires
//    public void onInventoryClick(InventoryClickEvent event) {
//        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
//        ItemStack clicked = event.getCurrentItem(); // The item that was clicked
//        Inventory inventory = event.getInventory(); // The inventory that was clicked in
//    }
//
//    // Méthode permettant de créer un item stack avec un nom/lore et l'ajouter dans l'inventaire
//    public static void createDisplay(Material material, Inventory inv, int Slot, String name, String lore, byte b) {
//        ItemStack item = new ItemStack(material, 1, b);
//        ItemMeta meta = item.getItemMeta();
//        meta.setDisplayName(name);
//        ArrayList<String> Lore = new ArrayList<String>();
//        Lore.add(lore);
//        meta.setLore(Lore);
//        item.setItemMeta(meta);
//
//        inv.setItem(Slot, item);
//
//    }

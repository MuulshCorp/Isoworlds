package bukkit.Utils;

/**
 * Created by Edwin on 23/11/2017.
 */

import bukkit.Commandes.SousCommandes.BiomeCommande;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
                            //sponge.Utils.IsoworldsUtils.cm("NAME MENU: " + clicked.getItemMeta().toString());
                            sponge.Utils.IsoworldsUtils.cm("PLAYER MENU: " + p.getName());
                            //if (clicked.getItemMeta().getDisplayName().equals("IsoWorlds")) {
                            BiomeCommande.menuBiome.open(p);
                            //}
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

//    // BIOME
//    public static IsoWorldsInventory menuBiome = new IsoWorldsInventory( IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds: Biome"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
//        @Override
//        public void onOptionClick(IsoWorldsInventory.OptionClickEvent event) {
//            event.getPlayer().sendMessage(ChatColor.GOLD + "[IsoWorlds] Vous entrez dans le menu: " + event.getName());
//            event.setWillClose(true);
//        }
//    }, instance)
//            .setOption(0, new ItemStack(Material.GRASS, 1), ChatColor.GOLD + "Plaines", "Un biome relativement plat avec des collines || vallonnées et une grande quantité de fleurs")
//            .setOption(1, new ItemStack(Material.SAND, 1), ChatColor.GREEN + "Désert", "Un biome constitué principalement de sable, de cactus et de canne à sucre.");



    public static IsoWorldsInventory getMenuPrincipal() {
        //MENU PRINCIPAL
        IsoWorldsInventory menuPrincipal = new IsoWorldsInventory(IsoworldsUtils.centerTitle(ChatColor.RED + "IsoWorlds"), 9, new IsoWorldsInventory.OptionClickEventHandler() {
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
}
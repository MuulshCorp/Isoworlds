package bukkit.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by Edwin on 23/11/2017.
 */
public class IsoWorldsInventory {
    // Inventaire principal, affiche toutes les commandes disponibles
    public static Inventory mainInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds -"));
    // The first parameter, is the inventory owner. I make it null to let everyone use it.
    //The second parameter, is the slots in a inventory. Must be a multiple of 9. Can be up to 54.
    //The third parameter, is the inventory name. This will accept chat colors.

    static {
        //inventory.setItem(0, new ItemStack(Material.DIRT, 1));
        //inventory.setItem(8, new ItemStack(Material.GOLD_BLOCK, 1));
        createDisplay(Material.DIRT, mainInventory, 0, ChatColor.GOLD + "Biome", "Gérez le biome des vos chunks", (byte) 1);
        createDisplay(Material.DIRT, mainInventory, 1, ChatColor.GREEN + "Confiance", "Gérez qui peut avoir accès à votre IsoWorld", (byte) 13);
        createDisplay(Material.DIRT, mainInventory, 2, ChatColor.GRAY + "Création/Refonte", "Créez, reformez votre IsoWorld", (byte) 7);
        createDisplay(Material.DIRT, mainInventory, 3, ChatColor.BLUE + "Maison", "Rendez-vous sur votre IsoWorld", (byte) 11);
        createDisplay(Material.DIRT, mainInventory, 3, ChatColor.YELLOW + "Météo", "Gérez la pluie et le beau temps de votre IsoWorld", (byte) 4);
        createDisplay(Material.DIRT, mainInventory, 4, ChatColor.RED + "Activation", "Chargez-Déchargez votre IsoWorld", (byte) 14);
        createDisplay(Material.DIRT, mainInventory, 5, ChatColor.LIGHT_PURPLE + "Téléportation", "Téléportez vous sur un IsoWorld [STAFF]", (byte) 10);
        createDisplay(Material.DIRT, mainInventory, 6, "Temps", "Gérez la le temps de votre IsoWorld", (byte) 0);
        //The first parameter, is the slot that is assigned to. Starts counting at 0
    }

    // BIOME
    public static Inventory biomeInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + ChatColor.GOLD + "Biome"));

    static {

        createDisplay(Material.DIRT, biomeInventory, 0, ChatColor.GOLD + "Biome", "Gérez le biome des vos chunks", (byte) 1);
    }

    // CONFIANCE
    public static Inventory confianceInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + ChatColor.GREEN + "Confiance"));

    static {

        createDisplay(Material.DIRT, confianceInventory, 0, ChatColor.GOLD + "Confiance", "Gérez le biome des vos chunks", (byte) 13);
    }

    // CREATION/REFONTE
    public static Inventory creationInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + ChatColor.GRAY + "Création/Refonte"));

    static {

        createDisplay(Material.DIRT, creationInventory, 0, ChatColor.GOLD + "Création/refonte", "Gérez le biome des vos chunks", (byte) 7);
    }

    // MAISON
    public static Inventory maisonInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + ChatColor.BLUE + "Maison"));

    static {

        createDisplay(Material.DIRT, maisonInventory, 0, ChatColor.GOLD + "Maison", "Gérez le biome des vos chunks", (byte) 11);
    }

    // METEO
    public static Inventory meteoInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + "Météo"));

    static {

        createDisplay(Material.DIRT, meteoInventory, 0, ChatColor.GOLD + "Météo", "Gérez le biome des vos chunks", (byte) 11);
    }

    // ACTIVATION
    public static Inventory activationInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + ChatColor.RED + "Activation"));

    static {

        createDisplay(Material.DIRT, activationInventory, 0, ChatColor.GOLD + "Activation", "Gérez le biome des vos chunks", (byte) 14);
    }

    // TELEPORTATION
    public static Inventory teleportationInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + ChatColor.LIGHT_PURPLE + "Téléporation"));

    static {

        createDisplay(Material.DIRT, teleportationInventory, 0, ChatColor.GOLD + "Téléporation", "Gérez le biome des vos chunks", (byte) 10);
    }

    // TEMPS
    public static Inventory tempsInventory = Bukkit.createInventory(null, 9, IsoworldsUtils.centerTitle(ChatColor.GOLD + "- IsoWorlds: " + ChatColor.YELLOW + "Temps"));

    static {

        createDisplay(Material.DIRT, tempsInventory, 0, ChatColor.GOLD + "Temps", "Gérez le biome des vos chunks", (byte) 4);
    }

    // Permet de récupérer les informations dans les invententaires
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
        ItemStack clicked = event.getCurrentItem(); // The item that was clicked
        Inventory inventory = event.getInventory(); // The inventory that was clicked in
    }

    // Méthode permettant de créer un item stack avec un nom/lore et l'ajouter dans l'inventaire
    public static void createDisplay(Material material, Inventory inv, int Slot, String name, String lore, byte b) {
        ItemStack item = new ItemStack(material, 1, b);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> Lore = new ArrayList<String>();
        Lore.add(lore);
        meta.setLore(Lore);
        item.setItemMeta(meta);

        inv.setItem(Slot, item);

    }
}

package bukkit.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Edwin on 23/11/2017.
 */
public class IsoWorldsInventory {
    public static Inventory inventory = Bukkit.createInventory(null, 9, "- IsoWorlds -");
    // The first parameter, is the inventory owner. I make it null to let everyone use it.
    //The second parameter, is the slots in a inventory. Must be a multiple of 9. Can be up to 54.
    //The third parameter, is the inventory name. This will accept chat colors.

    static {
        inventory.setItem(0, new ItemStack(Material.DIRT, 1));
        inventory.setItem(8, new ItemStack(Material.GOLD_BLOCK, 1));
        //The first parameter, is the slot that is assigned to. Starts counting at 0
    }

    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
        ItemStack clicked = event.getCurrentItem(); // The item that was clicked
        Inventory inventory = event.getInventory(); // The inventory that was clicked in
    }
}

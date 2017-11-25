package sponge.Utils;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;

import static sponge.IsoworldsSponge.instance;

/**
 * Created by Edwin on 25/11/2017.
 */
public class IsoWorldInventory {

    // MENU PRINCIPAL
    public static Inventory menuPrincipal(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("IsoWorlds")))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        ItemStack item = ItemStack.builder().itemType(ItemTypes.COAL).add(Keys.DISPLAY_NAME, Text.of("Test")).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item);

        return menu;
    }

    // BIOME
    public static Inventory menuBiome(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("IsoWorlds")))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        ItemStack item = ItemStack.builder().itemType(ItemTypes.COAL).add(Keys.DISPLAY_NAME, Text.of("Test")).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item);

        return menu;
    }

    // CONFIANCE
    public static Inventory menuConfiance(Player pPlayer) {

        Inventory menu = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .listener(ClickInventoryEvent.class, clickInventoryEvent -> {
                    // Code event
                    clickInventoryEvent.setCancelled(true);
                    IsoworldsUtils.cm("test");
                })
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("IsoWorlds")))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(9,1))
                .build(instance);

        ItemStack item = ItemStack.builder().itemType(ItemTypes.COAL).add(Keys.DISPLAY_NAME, Text.of("Test")).quantity(1).build();
        menu.query(SlotPos.of(4,0)).set(item);

        return menu;
    }
}

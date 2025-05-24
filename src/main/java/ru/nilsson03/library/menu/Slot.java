package ru.nilsson03.library.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.menu.item.impl.DynamicItem;

public class Slot {

    private final Character symbol;
    private final Item item;

    public Slot(Character symbol,
                Item item) {
        this.symbol = symbol;
        this.item = item;
    }

    public Character getSymbol() {
        return symbol;
    }

    public ItemStack getItemStack() {
        return item.getItemStack();
    }

    public void handleClick(InventoryClickEvent inventoryClickEvent) {
        item.onClick(inventoryClickEvent);
    }

    public void updateItem() {
        if (item instanceof DynamicItem dynamicItem) {
            dynamicItem.update();
        }
    }

    public Item getItem() {
        return item;
    }
}

package ru.nilsson03.library.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.menu.item.Item;

import java.util.List;

public interface Menu {

    List<String> getPattern();
    String getTitle();
    Inventory getInventory();
    void open(Player player);
    void close(Player player);
    boolean isCustomItem(Item item);

    default char getSymbolBySlot(int slot) {
        int row = slot / 9;
        int col = slot % 9;
        try {
            return getPattern().get(row).charAt(col);
        } catch (Exception e) {
            ConsoleLogger.debug("menulibrary", "Couldn't get the symbol by the slot number in the inventory, reason: %s", e.getMessage());
            throw new NullPointerException("Couldn't get the symbol by the slot number in the inventory");
        }
    }
}

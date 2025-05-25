package ru.nilsson03.library.menu.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.menu.Menu;
import ru.nilsson03.library.menu.Slot;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.menu.item.impl.CustomItem;

import java.util.*;

public class BaseMenu implements Menu {

    private final Inventory inventory;
    private final String title;
    private final int rows;
    private final List<Slot> slots = new ArrayList<>();
    private final List<String> pattern;

    public BaseMenu(String title, int rows, List<String> pattern) {
        if (title == null || title.isEmpty()) {
            ConsoleLogger.debug("menulibrary", "The title cannot be null or empty.");
            throw new NullPointerException("The title cannot be null or empty.");
        }

        if (rows < 9 || rows > 54) {
            ConsoleLogger.debug("menulibrary", "The inventory size cannot be less than 9 and not more than 54.");
            throw new IllegalArgumentException("The inventory size cannot be less than 9 and not more than 54.");
        }

        if (pattern == null || pattern.isEmpty()) {
            ConsoleLogger.debug("menulibrary", "The pattern cannot be null or empty.");
            throw new NullPointerException("The pattern cannot be null or empty.");
        }
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.title = title;
        this.pattern = pattern;
        this.rows = rows;
    }

    public void addItem(char symbol, Item item) {
        slots.add(new Slot(symbol, item));
    }

    @Override
    public void open(Player player) {
        fillInventory();
        Inventory inventory = getInventory();
        player.openInventory(inventory);
    }

    @Override
    public void close(Player player) {
        player.closeInventory();
    }

    @Override
    public boolean isCustomItem(Item item) {
        return item instanceof CustomItem;
    }

    public void handleClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory() != getInventory()) return;

        event.setCancelled(true);

        int eventSlot = event.getSlot();
        char symbol = getSymbolBySlot(eventSlot);

        Optional<Slot> slotOptional = getSlotBySymbol(symbol);
        if (slotOptional.isPresent()) {
            Slot slot = slotOptional.get();
            slot.handleClick(event);
        }
    }

    protected void fillInventory() {
        for (int row = 0; row < pattern.size(); row++) {
            String line = pattern.get(row);
            for (int col = 0; col < line.length(); col++) {
                char symbol = line.charAt(col);

                Optional<Slot> slotOptional = getSlotBySymbol(symbol);

                if (slotOptional.isPresent()) {

                    int position = row * 9 + col;
                    Slot slot = slotOptional.get();

                    inventory.setItem(position,
                            slot.getItem().getItemStack());

                    ConsoleLogger.debug("menulibrary", "Item %s has been installed in the %s slot of %s inventory",
                            slot.getItem().getItemStack().getType(),
                            position, title);
                } else {
                    ConsoleLogger.warn("menulibrary", "Couldn't get the slot by the symbol %s in the inventory",
                            symbol);
                }
            }
        }
    }

    protected Optional<Slot> getSlotBySymbol(char symbol) {
        return this.slots.stream()
                .filter(slot -> slot.getSymbol().equals(symbol))
                .findFirst();
    }

    @Override
    public List<String> getPattern() {
        return pattern;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getRows() {
        return rows;
    }
}

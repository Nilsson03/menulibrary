package ru.nilsson03.library.menu.impl;

import org.bukkit.event.inventory.InventoryClickEvent;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.menu.Slot;
import ru.nilsson03.library.menu.virtual.VirtualInventory;
import ru.nilsson03.library.menu.virtual.VirtualInventoryManager;

import java.util.*;

public class InteractiveMenu extends BaseMenu {

    private final Set<Character> interactiveSymbols = new HashSet<>();
    private VirtualInventory virtualInventory;

    public InteractiveMenu(String title, int rows, List<String> pattern) {
        super(title, rows, pattern);
    }

    public InteractiveMenu(String title, int rows, List<String> pattern, String id, UUID owner) {
        super(title, rows, pattern);
        this.virtualInventory = new VirtualInventory(id, owner, getInventory());
    }

    public void allowInteractionFor(char symbol) {
        interactiveSymbols.add(symbol);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory() != getInventory()) {
            return;
        }

        int clickedSlot = event.getSlot();
        char symbol = getSymbolBySlot(clickedSlot);

        if (!interactiveSymbols.contains(symbol)) {
            Optional<Slot> slotOptional = getSlotBySymbol(symbol);
            if (slotOptional.isPresent()) {
                Slot slot = slotOptional.get();
                slot.handleClick(event);
                event.setCancelled(true);
            }
        }
    }

    public void saveVirtualInventory() {
        if (virtualInventory != null) {
            VirtualInventoryManager.save(virtualInventory);
        }
    }

    public void loadVirtualInventory() {
        if (virtualInventory != null) {
            VirtualInventoryManager.load(
                    virtualInventory.getId()
            );
        } else {
            ConsoleLogger.warn("menulibrary", "It is not possible to load the virtual inventory for InteractiveMenu because virtualInventory is null");
        }
    }
}

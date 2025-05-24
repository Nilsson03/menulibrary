package ru.nilsson03.library.menu.item.impl.control;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.menu.Menu;
import ru.nilsson03.library.menu.impl.PagedMenu;

public abstract class PageItem extends ControlItem {

    private final boolean forward;

    public PageItem(boolean forward) {
        this.forward = forward;
    }

    public void onClick(InventoryClickEvent event) {
        ConsoleLogger.debug("menulibrary", "======= PageItem onClick() =======");

        Menu menu = getMenu();

        if (menu != null) {
            event.setCancelled(true);
            ConsoleLogger.debug("menulibrary", "The click action is completed.");
            if (menu instanceof PagedMenu pagedMenu) {
                ConsoleLogger.debug("menulibrary", "Menu is paged.");
                if (event.getWhoClicked() instanceof Player player) {
                    if (forward) {
                        ConsoleLogger.debug("menulibrary", "Forwarding page in inventory for player (forward == true).");
                        pagedMenu.forward(player);
                    } else {
                        ConsoleLogger.debug("menulibrary", "Forwarding page in inventory for player (forward == false).");
                        pagedMenu.backward(player);
                    }
                }
            }
        }
        ConsoleLogger.debug("menulibrary", "Menu is null, skipped.");
    }

    @Override
    public void update() {
        // empty
    }
}

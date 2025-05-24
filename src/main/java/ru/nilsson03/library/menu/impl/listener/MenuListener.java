package ru.nilsson03.library.menu.impl.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.menu.Menu;
import ru.nilsson03.library.menu.impl.Window;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu) {
            ConsoleLogger.debug("menulibrary", "======= InventoryClickEvent onClick() =======");
            event.setCancelled(true);
            ConsoleLogger.debug("menulibrary", "The standard behavior has been canceled");
            Player player = (Player) event.getWhoClicked();
            Window window = Window.of(player);
            window.handleClick(event);
            ConsoleLogger.debug("menulibrary", "The handleClick method has been called!");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Menu) {
            ConsoleLogger.debug("menulibrary", "======= InventoryClickEvent onClose() =======");
            Player player = (Player) event.getPlayer();
            Window window = Window.of(player);
            window.close(player);
        }
    }
}

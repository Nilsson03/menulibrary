package ru.nilsson03.library.menu.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import ru.nilsson03.library.menu.Menu;

import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Window {

    private static final Map<UUID, Window> WINDOWS = new ConcurrentHashMap<>();

    private final Map<Player, Menu> openMenus = new ConcurrentHashMap<>();
    private final Map<Player, Integer> currentPages = new ConcurrentHashMap<>();

    public static Window of(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return WINDOWS.computeIfAbsent(player.getUniqueId(), k -> new Window());
    }

    public void open(Player player, Menu menu) {
        if (player == null || menu == null) {
            throw new IllegalArgumentException("Player and menu cannot be null");
        }

        close(player);

        openMenus.put(player, menu);
        if (menu instanceof PagedMenu) {
            currentPages.put(player, 0);
        }

        menu.open(player);
    }

    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Menu menu = openMenus.get(player);
        if (menu == null) {
            return;
        }

        if (menu instanceof BaseMenu baseMenu) {
            baseMenu.handleClick(event);
        } else if (menu instanceof PagedMenu pagedMenu) {
            int currentPage = currentPages.getOrDefault(player, 0);
            pagedMenu.handleClick(event, currentPage);
        }
    }

    public void setPage(Player player, int page) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        Menu menu = openMenus.get(player);
        if (menu instanceof PagedMenu) {
            currentPages.put(player, page);
        }
    }

    public void close(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        Menu menu = openMenus.remove(player);
        currentPages.remove(player);

        if (menu != null) {
            menu.close(player);
        }
    }

    public OptionalInt getCurrentPage(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        Integer page = currentPages.get(player);
        return page != null ? OptionalInt.of(page) : OptionalInt.empty();
    }
}

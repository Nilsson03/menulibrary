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
import ru.nilsson03.library.menu.item.impl.control.ControlItem;
import ru.nilsson03.library.menu.item.impl.control.PageItem;

import java.util.*;

public class PagedMenu implements Menu {

    private final Inventory inventory;
    private final String title;
    private final Map<Integer, Map<Integer, Slot>> pages = new HashMap<>();
    private final List<String> pattern;
    private Character fillSymbol;
    private final Map<Character, List<Integer>> symbolSlots;

    private final Map<Integer, Inventory> cachedStaticPages = new HashMap<>();
    private final Map<Integer, Set<Integer>> dynamicSlots = new HashMap<>();

    public PagedMenu(String title, int rows, List<String> pattern) {
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
        this.inventory = Bukkit.createInventory(null, rows * 9, title + " (Стр. " + 1 + ")");
        this.title = title;
        this.pattern = pattern;
        this.symbolSlots = buildSymbolMap(pattern);
        this.pages.put(0, new HashMap<>());
        cacheStaticParts();
    }

    private void cacheStaticParts() {
        for (int page : pages.keySet()) {
            Inventory inv = Bukkit.createInventory(null, inventory.getSize(), title + " (Стр. " + (page + 1) + ")");

            pages.get(page).forEach((slot, slotItem) -> {
                if (slotItem.getSymbol() != fillSymbol) {
                    inv.setItem(slot, slotItem.getItemStack());
                }
            });

            cachedStaticPages.put(page, inv);
        }
    }

    // Метод используется для обновления предметов-заполнителей при переоткрытии инвентаря
    public void updateDynamicItems(List<Item> newItems) {
        int itemsPerPage = dynamicSlots.get(0).size();
        int totalPages = (int) Math.ceil((double) newItems.size() / itemsPerPage);

        // Если изменилось количество - пересоздаём
        while (pages.size() < totalPages) {
            int newPage = pages.size();
            pages.put(newPage, new HashMap<>());
            dynamicSlots.put(newPage, new HashSet<>(dynamicSlots.get(0)));
        }

        // Обновляем предметы
        int itemIndex = 0;
        for (int page = 0; page < pages.size(); page++) {
            Inventory inv = cachedStaticPages.get(page);
            for (int slot : dynamicSlots.get(page)) {
                if (itemIndex < newItems.size()) {
                    inv.setItem(slot, newItems.get(itemIndex++).getItemStack());
                } else {
                    inv.setItem(slot, null);
                }
            }
        }
    }

    public void addItems(List<Item> items) {
        List<Integer> fillSlots = symbolSlots.getOrDefault(fillSymbol, List.of());

        int itemsAdded = 0;
        int currentPage = 0;

        while (itemsAdded < items.size()) {
            Map<Integer, Slot> page = pages.computeIfAbsent(currentPage, k -> new HashMap<>());

            for (int slot : fillSlots) {
                if (itemsAdded >= items.size()) break;
                if (!page.containsKey(slot)) {
                    page.put(slot, new Slot(fillSymbol, items.get(itemsAdded++)));
                }
            }

            if (itemsAdded < items.size()) {
                currentPage++;
            }
        }
    }

    public void setItem(char symbol, Item item) {
        if (!symbolSlots.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' not in pattern");
        }

        for (int slot : symbolSlots.get(symbol)) {
            int page = findAvailablePage(slot);

            if (item instanceof ControlItem controlItem) {
                controlItem.setMenu(this);
            }

            pages.get(page).put(slot, new Slot(symbol, item));
        }
    }

    private int findAvailablePage(int slot) {
        for (int page : pages.keySet()) {
            if (!pages.get(page).containsKey(slot)) {
                return page;
            }
        }
        int newPage = pages.size();
        pages.put(newPage, new HashMap<>());
        return newPage;
    }

    private Map<Character, List<Integer>> buildSymbolMap(List<String> pattern) {
        Map<Character, List<Integer>> map = new HashMap<>();
        for (int row = 0; row < pattern.size(); row++) {
            String line = pattern.get(row);
            for (int col = 0; col < line.length(); col++) {
                char symbol = line.charAt(col);
                map.computeIfAbsent(symbol, k -> new ArrayList<>())
                        .add(row * 9 + col);
            }
        }
        return map;
    }

    @Override
    public void open(Player player) {
        loadPage(0);
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

    public void handleClick(InventoryClickEvent event, int currentPage) {

        if (!pages.containsKey(currentPage)) {
            ConsoleLogger.debug("menulibrary", "Menu pages don't contain a %s page.", currentPage);
        }

        int slotNumber = event.getSlot();
        Slot slot = pages.get(currentPage).get(slotNumber);

        if (slot != null) {

            if (isNavigationItem(slot)) {
                clickNavigationItem(event, currentPage);
            } else {
                slot.handleClick(event);
            }
        }
    }

    private boolean isNavigationItem(Slot slot) {
        Item item = slot.getItem();
        return item instanceof PageItem;
    }

    private void clickNavigationItem(InventoryClickEvent inventoryClickEvent, int currentPage) {

        if (currentPage < 0 || !pages.containsKey(currentPage)) {
            ConsoleLogger.debug("menulibrary", "The page %s does not exist. (max: %s, clickNavigationItem()).", currentPage, pages.size());
            throw new IllegalArgumentException("Invalid page number");
        }

        int slot = inventoryClickEvent.getSlot();
        Map<Integer, Slot> items = this.pages.get(currentPage);

        Slot slotObject = items.get(slot);
        if (slotObject.getItem() instanceof PageItem pageItem) {
            pageItem.onClick(inventoryClickEvent);
        }
    }

    public void forward(Player player) {
        Window window = Window.of(player);
        OptionalInt currentPageOptional = window.getCurrentPage(player);

        if (currentPageOptional.isEmpty()) {
            ConsoleLogger.warn("menulibrary", "An error has occurred in which there is no record of the player's current page in the open PagedMenu. The first page is installed and loaded.");
            window.setPage(player, 0);
            loadPage(0);
            return;
        }

        int nextPage = currentPageOptional.getAsInt() + 1;

        if (pages.containsKey(nextPage)) {
            window.setPage(player, nextPage);
            loadPage(nextPage);
        }
    }

    public void backward(Player player) {
        Window window = Window.of(player);
        OptionalInt currentPageOptional = window.getCurrentPage(player);

        if (currentPageOptional.isEmpty()) {
            ConsoleLogger.warn("menulibrary", "An error has occurred in which there is no record of the player's current page in the open PagedMenu. The first page is installed and loaded.");
            window.setPage(player, 0);
            loadPage(0);
            return;
        }

        int previousPage = currentPageOptional.getAsInt() - 1;

        if (pages.containsKey(previousPage)) {
            window.setPage(player, previousPage);
            loadPage(previousPage);
        }
    }

    private void loadPage(int page) {
        if (page < 0 || !pages.containsKey(page)) {
            ConsoleLogger.debug("menulibrary", "The page %s does not exist. (max: %s).", page, pages.size());
            throw new IllegalArgumentException("Invalid page number");
        }

        Inventory inventory = getInventory();
        inventory.clear();
        pages.get(page).forEach((slot, item) -> getInventory().setItem(slot, item.getItemStack()));
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

    public void setFillSymbol(Character fillSymbol) {
        this.fillSymbol = fillSymbol;
    }
}

package ru.nilsson03.library.menu.builder;

import ru.nilsson03.library.menu.cache.MenuCache;
import ru.nilsson03.library.menu.impl.BaseMenu;
import ru.nilsson03.library.menu.impl.InteractiveMenu;
import ru.nilsson03.library.menu.impl.PagedMenu;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.text.api.impl.UniversalTextApi;

import java.util.*;

public class MenuBuilder {
    private String title;
    private int rows = 3;
    private List<String> pattern;
    private char fillSymbol = 'x';
    private final Map<Character, Item> symbolItems = new HashMap<>();
    private final List<Item> fillItems = new ArrayList<>();
    private boolean paged = false;
    private boolean interactive = false;
    private String virtualInventoryId;
    private UUID ownerUUID;
    private final Set<Character> interactiveSymbols = new HashSet<>();

    public MenuBuilder title(String title) {
        this.title = UniversalTextApi.colorize(
                Objects.requireNonNullElse(title, "Menu")
        );
        return this;
    }

    public MenuBuilder rows(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }
        this.rows = rows;
        return this;
    }

    public MenuBuilder pattern(String... patternLines) {
        this.pattern = Arrays.asList(patternLines);
        if (!validatePattern()) {
            throw new IllegalArgumentException("Invalid menu pattern");
        }
        return this;
    }

    public MenuBuilder fillSymbol(char symbol) {
        this.fillSymbol = symbol;
        return this;
    }

    public MenuBuilder asPaged() {
        this.paged = true;
        return this;
    }

    public MenuBuilder asInteractive() {
        this.interactive = true;
        return this;
    }

    public MenuBuilder withVirtualInventory(String id, UUID owner) {
        this.virtualInventoryId = id;
        this.ownerUUID = owner;
        return this;
    }

    public MenuBuilder allowInteractionFor(char symbol) {
        this.interactiveSymbols.add(symbol);
        return this;
    }

    public MenuBuilder addFillItem(Item item) {
        this.fillItems.add(item);
        return this;
    }

    public MenuBuilder addFillItems(Collection<Item> items) {
        this.fillItems.addAll(items);
        return this;
    }

    public MenuBuilder addItem(char symbol, Item item) {
        this.symbolItems.put(symbol, item);
        return this;
    }

    public BaseMenu buildBase() {
        validateBuild();

        String templateKey = generateTemplateKey();

        if (MenuCache.hasTemplate(templateKey)) {
            return MenuCache.getTemplate(templateKey, BaseMenu.class);
        }

        BaseMenu menu = new BaseMenu(title, rows, pattern);
        symbolItems.forEach(menu::addItem);
        return menu;
    }

    public PagedMenu buildPaged() {
        validateBuild();

        String templateKey = generateTemplateKey();

        if (MenuCache.hasTemplate(templateKey)) {
            return MenuCache.getTemplate(templateKey, PagedMenu.class);
        }

        PagedMenu menu = new PagedMenu(title, rows, pattern);

        menu.setFillSymbol(fillSymbol);

        if (!fillItems.isEmpty()) {
            menu.addItems(fillItems);
        }

        symbolItems.forEach(menu::setItem);

        return menu;
    }

    public InteractiveMenu buildInteractive() {
        validateBuild();
        InteractiveMenu menu;

        if (virtualInventoryId != null && ownerUUID != null) {
            menu = new InteractiveMenu(title, rows, pattern, virtualInventoryId, ownerUUID);
        } else {
            menu = new InteractiveMenu(title, rows, pattern);
        }

        interactiveSymbols.forEach(menu::allowInteractionFor);
        symbolItems.forEach(menu::addItem);

        return menu;
    }

    public Object build() {
        if (paged) return buildPaged();
        if (interactive) return buildInteractive();
        return buildBase();
    }

    private void validateBuild() {
        Objects.requireNonNull(title, "Title must be set");
        Objects.requireNonNull(pattern, "Pattern must be set");

        if (paged) {
            long fillSlots = pattern.stream()
                    .flatMapToInt(String::chars)
                    .filter(c -> c == fillSymbol)
                    .count();

            if (fillSlots == 0) {
                ConsoleLogger.warn("menulibrary",
                        "No fill slots ('%c') found in pattern", fillSymbol);
            }
        }
    }

    private boolean validatePattern() {
        if (pattern == null || pattern.isEmpty()) {
            ConsoleLogger.error("menulibrary", "Pattern cannot be empty");
            return false;
        }

        if (pattern.size() > rows) {
            ConsoleLogger.error("menulibrary",
                    "Pattern has %d rows, but inventory has only %d",
                    pattern.size(), rows);
            return false;
        }

        for (String line : pattern) {
            if (line.length() != 9) {
                ConsoleLogger.error("menulibrary",
                        "Line must be 9 chars long, found %d: '%s'",
                        line.length(), line);
                return false;
            }
        }

        return true;
    }

    private String generateTemplateKey() {
        return String.format("menu_%s_rows_%d_pattern_%s",
                title, rows, String.join("|", pattern));
    }
}
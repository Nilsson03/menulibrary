package ru.nilsson03.library.menu.parser;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ru.nilsson03.library.NPlugin;
import ru.nilsson03.library.menu.Menu;
import ru.nilsson03.library.menu.MenuLibrary;
import ru.nilsson03.library.menu.impl.BaseMenu;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.menu.item.impl.CustomItem;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;

import java.util.List;

public class MenuParser {

    private static final NPlugin plugin = MenuLibrary.getInstance();

    public static Menu parse(ConfigurationSection section) throws NullPointerException {
        ConfigurationSection itemsSection = section.getConfigurationSection("items");

        if (itemsSection == null) {
            ConsoleLogger.debug(plugin, "Could not load menu from configuration section, because section is null. Class MenuParser, section %s", section.getName());
            throw new NullPointerException("Could not load menu from configuration section, because section is null");
        }

        String title = section.getString("title");
        int rows = section.getInt("rows");
        List<String> pattern = section.getStringList( "pattern");
        BaseMenu menu = new BaseMenu(title, rows, pattern);

        for (String key : itemsSection.getKeys(false)) {
            char symbol = key.charAt(0);
            ConfigurationSection itemConfig = itemsSection.getConfigurationSection(key);
            if (itemConfig == null) {
                ConsoleLogger.warn(plugin, "Could not get item section for %s from configuration", key);
                continue;
            }
            Item item = new CustomItem(plugin, itemConfig);

            menu.addItem(symbol, item);
        }
        return menu;
    }
}

package ru.nilsson03.library.menu.item.util;

import org.bukkit.configuration.ConfigurationSection;
import ru.nilsson03.library.NPlugin;
import ru.nilsson03.library.bukkit.item.builder.SkullItemBuilder;
import ru.nilsson03.library.bukkit.item.builder.impl.UniversalSkullBuilder;
import ru.nilsson03.library.bukkit.item.skull.SkullTextureHandler;
import ru.nilsson03.library.bukkit.item.skull.factory.SkullHandlerFactory;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.menu.item.impl.CustomItem;

import java.util.*;

public class ItemUtil {

    private static final SkullTextureHandler handler = SkullHandlerFactory.createHandler();
    private static final SkullItemBuilder builder = new UniversalSkullBuilder(handler);

    public static SkullItemBuilder createHead(String url) {
        return builder.setSkinTexture(url);
    }

    public static List<CustomItem> parseSection(NPlugin plugin, ConfigurationSection section) {
        Objects.requireNonNull(section, "Items section cannot be null");
        List<CustomItem> customItems = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            char symbol = key.charAt(0);
            ConfigurationSection itemConfig = section.getConfigurationSection(key);
            if (itemConfig == null) {
                ConsoleLogger.warn(plugin, "Could not get item section for %s from configuration", key);
                continue;
            }
            CustomItem item = new CustomItem(plugin, itemConfig);

            customItems.add(item);
        }
        return customItems;
    }
}

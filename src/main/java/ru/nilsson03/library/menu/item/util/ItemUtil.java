package ru.nilsson03.library.menu.item.util;

import ru.nilsson03.library.bukkit.item.builder.SkullItemBuilder;
import ru.nilsson03.library.bukkit.item.builder.impl.UniversalSkullBuilder;
import ru.nilsson03.library.bukkit.item.skull.SkullTextureHandler;
import ru.nilsson03.library.bukkit.item.skull.factory.SkullHandlerFactory;

public class ItemUtil {

    private static final SkullTextureHandler handler = SkullHandlerFactory.createHandler();
    private static final SkullItemBuilder builder = new UniversalSkullBuilder(handler);

    public static SkullItemBuilder createHead(String url) {
        return builder.setSkinTexture(url);
    }
}

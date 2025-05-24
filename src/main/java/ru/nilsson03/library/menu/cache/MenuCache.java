package ru.nilsson03.library.menu.cache;

import ru.nilsson03.library.menu.Menu;
import ru.nilsson03.library.menu.impl.BaseMenu;
import ru.nilsson03.library.menu.impl.PagedMenu;
import java.util.HashMap;
import java.util.Map;

public class MenuCache {
    private static final Map<String, Menu> TEMPLATE_CACHE = new HashMap<>();

    // Кэширование с проверкой типа
    public static <T extends Menu> void cacheTemplate(String key, T menu) {
        TEMPLATE_CACHE.put(key, menu);
    }

    // Получение с приведением типа
    @SuppressWarnings("unchecked")
    public static <T extends Menu> T getTemplate(String key, Class<T> menuClass) {
        if (TEMPLATE_CACHE.containsKey(key)) {
            Menu menu = TEMPLATE_CACHE.get(key);
            if (menuClass.isInstance(menu)) {
                return (T) menu;
            }
        }
        return null;
    }

    public static Menu getTemplate(String key) {
        return TEMPLATE_CACHE.get(key);
    }

    public static boolean hasTemplate(String key) {
        return TEMPLATE_CACHE.containsKey(key);
    }

    public static void invalidateTemplate(String key) {
        TEMPLATE_CACHE.remove(key);
    }

    public static void clearAll() {
        TEMPLATE_CACHE.clear();
    }

    // Специфичные методы для разных типов меню
    public static BaseMenu getBaseMenuTemplate(String key) {
        return getTemplate(key, BaseMenu.class);
    }

    public static PagedMenu getPagedMenuTemplate(String key) {
        return getTemplate(key, PagedMenu.class);
    }
}

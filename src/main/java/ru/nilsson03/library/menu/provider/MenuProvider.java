package ru.nilsson03.library.menu.provider;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import ru.nilsson03.library.NPlugin;
import ru.nilsson03.library.bukkit.file.FileHelper;
import ru.nilsson03.library.menu.item.scheduler.ItemUpdateScheduler;
import ru.nilsson03.library.menu.item.Updatable;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;

import java.util.*;

public class MenuProvider {

    public static final Map<NPlugin, PluginInfo> registeredProviders;

    static {
        registeredProviders = new HashMap<>();
    }

    private final Plugin plugin;
    private final ItemUpdateScheduler itemUpdateScheduler;

    public MenuProvider(NPlugin plugin) {

        if (plugin == null) {
            ConsoleLogger.debug("menulibrary", "Error initializing the %s class. Plugin cannot be null.", this.getClass().getName());
            throw new NullPointerException("Plugin cannot be null.");
        }

        if (registeredProviders.containsKey(plugin)) {
            ConsoleLogger.debug("menulibrary", "MenuProvider already registered for plugin " + plugin.getName());
            throw new IllegalStateException("MenuProvider already registered for plugin " + plugin.getName());
        }

        FileHelper.createFileOrLoad(plugin, "inventories");

        this.plugin = plugin;
        this.itemUpdateScheduler = new ItemUpdateScheduler(plugin);

        registeredProviders.put(plugin, new PluginInfo(plugin, this));
        ConsoleLogger.debug("menulibrary", "Registered MenuProvider for plugin " + plugin.getName());
    }

    public static Optional<MenuProvider> get(NPlugin plugin) {
        if (registeredProviders.containsKey(plugin)) {
            ConsoleLogger.warn(plugin, "MenuProvider is not registered for the %s plugin", plugin.getName());
            return Optional.empty();
        }

        PluginInfo pluginInfo = registeredProviders.get(plugin);

        return Optional.of(pluginInfo.getProvider());
    }

    public void addNewUpdatableItem(Updatable updatable) {
        Preconditions.checkArgument(itemUpdateScheduler != null, "itemUpdateScheduler cannot be null");
        ConsoleLogger.debug("menulibrary", "itemUpdateScheduler cannot be null. Plugin %s, method %s", plugin.getName(), "addNewUpdatableItem");
        itemUpdateScheduler.registerUpdatable(updatable);
    }

    public void cancelUpdateScheduler() {
        Preconditions.checkArgument(itemUpdateScheduler != null, "itemUpdateScheduler cannot be null");
        ConsoleLogger.debug("menulibrary", "itemUpdateScheduler cannot be null. Plugin %s, method %s", plugin.getName(), "cancelUpdateScheduler");
        itemUpdateScheduler.cancelTask();
    }

    public Plugin getPlugin() {
        return plugin;
    }
}

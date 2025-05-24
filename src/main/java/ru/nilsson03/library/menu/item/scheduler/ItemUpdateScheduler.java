package ru.nilsson03.library.menu.item.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.nilsson03.library.menu.item.Updatable;
import ru.nilsson03.library.menu.item.impl.DynamicItem;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ItemUpdateScheduler {
    private final Plugin plugin;
    private final Set<Updatable> updatables = ConcurrentHashMap.newKeySet();
    private final BukkitTask bukkitTask;
    private static int currentSecond = 0;

    public ItemUpdateScheduler(JavaPlugin plugin) {
        this(plugin, new ArrayList<>());
    }

    public ItemUpdateScheduler(JavaPlugin plugin, List<DynamicItem> dynamicItems) throws NullPointerException {
        if (plugin == null) {
            ConsoleLogger.debug("menulibrary", "Error initializing the %s class. Plugin cannot be null.", this.getClass().getName());
            throw new NullPointerException("Plugin cannot be null.");
        }
        if (dynamicItems != null && !dynamicItems.isEmpty()) {
            ConsoleLogger.debug("menulibrary", "Error initializing the %s class. DynamicItems cannot be null.", this.getClass().getName());
            throw new NullPointerException("DynamicItems cannot be null.");
        }
        this.plugin = plugin;
        this.bukkitTask = startScheduler();
    }

    public void registerUpdatable(Updatable updatable) {
        updatables.add(updatable);
    }

    public BukkitTask startScheduler() {
        return Bukkit.getScheduler().runTaskTimer(plugin,
                () -> {
            currentSecond++;
            updateAllItems();
        }, 0L, 20L);
    }

    private void updateAllItems() {
        for (Updatable dynamicItem : updatables) {
            if (currentSecond % dynamicItem.getUpdateInterval() == 0) {
                dynamicItem.update();
            }
        }
    }

    public void cancelTask() {
        Objects.requireNonNull(bukkitTask, "BukkitTask cannot be null");
        bukkitTask.cancel();
    }

    public Set<Updatable> getUpdatables() {
        return updatables;
    }
}

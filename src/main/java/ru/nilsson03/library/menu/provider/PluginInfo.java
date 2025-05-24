package ru.nilsson03.library.menu.provider;

import ru.nilsson03.library.NPlugin;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class PluginInfo {

    private final NPlugin plugin;
    private String inventoriesFolder;
    private final MenuProvider menuProvider;

    public PluginInfo(NPlugin plugin,
                      MenuProvider menuProvider) {
        if (plugin == null) {
            ConsoleLogger.debug("menulibrary", "Couldn't initialize PluginInfo for MenuLibrary integration because plugin is null.");
            throw new IllegalArgumentException("Couldn't initialize PluginInfo for MenuLibrary integration because plugin is null.");
        }

        if (menuProvider == null) {
            ConsoleLogger.debug("menulibrary", "Couldn't initialize PluginInfo for MenuLibrary integration because menuProvider is null.");
            throw new IllegalArgumentException("Couldn't initialize PluginInfo for MenuLibrary integration because menuProvider is null.");
        }

        this.plugin = plugin;
        this.menuProvider = menuProvider;
    }

    public void setFolder(File folder) {
        Objects.requireNonNull(folder, "Inventories folder cant be null");
        inventoriesFolder = folder.getAbsolutePath();
    }

    public Optional<File> getInventoriesFolder() {
        if (inventoriesFolder == null || inventoriesFolder.trim().isEmpty()) {
            ConsoleLogger.warn(plugin, "No inventories folder specified");
            return Optional.empty();
        }

        return Optional.of(new File(inventoriesFolder));
    }

    public MenuProvider getProvider() {
        return menuProvider;
    }
}

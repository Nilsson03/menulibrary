package ru.nilsson03.library.menu;

import ru.nilsson03.library.NPlugin;
import ru.nilsson03.library.menu.impl.listener.MenuListener;
import ru.nilsson03.library.bukkit.integration.VersionChecker;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;
import ru.nilsson03.library.menu.provider.MenuProvider;
import ru.nilsson03.library.menu.provider.PluginInfo;
import ru.nilsson03.library.menu.virtual.VirtualInventoryManager;

import java.util.Map;

public class MenuLibrary extends NPlugin {

    private static MenuLibrary instance;

    @Override
    public void enable() {

        String baseLibraryVersion = getBaseLibrary().getDescription().getVersion();
        String minimalVersion = "1.0.2-RELEASE";
        if (!VersionChecker.isCompatible(baseLibraryVersion, minimalVersion)) {
            ConsoleLogger.error(this, "The installed version of BaseLibrary is not suitable (current %s, minimum %s)", baseLibraryVersion, minimalVersion);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        ConsoleLogger.info(this, "MenuLibrary version %s has been successfully enabled.", getDescription().getVersion());
    }

    @Override
    public void disable() {
        for (Map.Entry<NPlugin, PluginInfo> entry : MenuProvider.registeredProviders.entrySet()) {
            PluginInfo info = entry.getValue();
            MenuProvider provider = info.getProvider();
            if (provider == null) continue;
            provider.cancelUpdateScheduler();
        }

        VirtualInventoryManager.clearCache();
    }

    public static MenuLibrary getInstance() {
        return instance;
    }
}

package ru.nilsson03.library.menu.virtual;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class VirtualInventoryManager {
    private static final String SAVE_FOLDER = "plugins/menulibrary/virtual/";
    private static final Map<String, VirtualInventory> cache = new HashMap<>();

    public static void save(VirtualInventory virtualInventory) {
        File folder = new File(SAVE_FOLDER);
        if (!folder.exists()) folder.mkdirs();

        File file = new File(folder, virtualInventory.getId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("id", virtualInventory.getId());
        config.set("owner", virtualInventory.getOwner().toString());

        // Сохраняем предметы
        ItemStack[] contents = virtualInventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                config.set("items." + i, contents[i]);
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            ConsoleLogger.error("menulibrary", "Failed to save virtual inventory with id %s", virtualInventory.getId());
        }
    }

    public static VirtualInventory load(String id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }

        File file = new File(SAVE_FOLDER, id + ".yml");
        if (!file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        UUID owner = UUID.fromString(config.getString("owner"));
        String title = config.getString("title");

        VirtualInventory inventory = new VirtualInventory(id, owner,
                Bukkit.createInventory(null, 27, title)); // Размер по умолчанию 27

        if (config.contains("items")) {
            for (String key : config.getConfigurationSection("items").getKeys(false)) {
                int slot = Integer.parseInt(key);
                inventory.getInventory().setItem(slot, config.getItemStack("items." + key));
            }
        }

        cache.put(id, inventory);
        return inventory;
    }

    public static List<VirtualInventory> findByOwner(UUID owner, boolean all) {
        List<VirtualInventory> result = new ArrayList<>();

        for (VirtualInventory inv : cache.values()) {
            if (inv.getOwner().equals(owner)) {
                result.add(inv);
            }
        }

        if (all) {
            File folder = new File(SAVE_FOLDER);
            if (folder.exists()) {
                for (File file : folder.listFiles()) {
                    if (!file.getName().endsWith(".yml")) continue;

                    String id = file.getName().replace(".yml", "");
                    if (!cache.containsKey(id)) {
                        VirtualInventory inv = load(id);
                        if (inv != null && inv.getOwner().equals(owner)) {
                            result.add(inv);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static void saveAll() {
        for (VirtualInventory inv : cache.values()) {
            save(inv);
        }
    }

    public static void delete(String id) {
        cache.remove(id);
        File file = new File(SAVE_FOLDER, id + ".yml");
        if (file.exists()) file.delete();
    }

    public static void clearCache() {
        saveAll();
        cache.clear();
    }
}

package ru.nilsson03.library.menu.item.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import ru.nilsson03.library.NPlugin;
import ru.nilsson03.library.menu.provider.MenuProvider;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.menu.item.Updatable;
import ru.nilsson03.library.menu.item.util.ItemUtil;
import ru.nilsson03.library.bukkit.item.builder.ItemBuilder;
import ru.nilsson03.library.bukkit.item.builder.impl.SpigotItemBuilder;

import java.util.List;

public class CustomItem extends Item implements Updatable {
    private final ConfigurationSection config;
    private final Plugin plugin;
    private ItemStack currentItem;
    private int currentFrame = 0;
    private final int updateInterval;

    public CustomItem(NPlugin plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.config = config;
        boolean isDynamic = config.contains("update-interval");
        this.updateInterval = config.getInt("update-interval", 1);
        this.currentItem = buildItem();

        if (isDynamic) {
            MenuProvider.get(plugin).ifPresent(provider ->
                    provider.addNewUpdatableItem(this));
        }
    }

    @Override
    public ItemStack getItemStack() {
        return currentItem.clone();
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getWhoClicked() instanceof Player player) {
            config.getStringList("click-commands").forEach(cmd ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            cmd.replace("%player%", player.getName()))
            );
        }
    }

    @Override
    public void update() {
        if (config.contains("animated-name")) {
            currentFrame = (currentFrame + 1) % config.getStringList("animated-name").size();
        }
        this.currentItem = buildItem();
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    private ItemStack buildItem() {
        ItemStack item = createBaseItem();
        ItemMeta meta = item.getItemMeta();

        ItemBuilder builder = new SpigotItemBuilder(item)
                .setMeta(meta);

        if (config.contains("animated-name")) {
            List<String> frames = config.getStringList("animated-name");
            builder.setDisplayName(frames.get(currentFrame));
        } else {
            builder.setDisplayName(config.getString("name"));
        }

        if (config.contains("lore")) {
            builder.setLore(config.getStringList("lore"));
        }

        return builder.build();
    }

    private ItemStack createBaseItem() {
        if (config.getString("type", "material").equalsIgnoreCase("head")) {
            return createHeadItem(config.getString("head-id"));
        }
        return new ItemStack(Material.valueOf(config.getString("material")));
    }

    private ItemStack createHeadItem(String texture) {
        return ItemUtil.createHead(texture)
                .build();
    }

    public Plugin getPlugin() {
        return plugin;
    }
}

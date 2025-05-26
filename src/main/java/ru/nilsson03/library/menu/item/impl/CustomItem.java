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
import ru.nilsson03.library.text.api.TextApi;
import ru.nilsson03.library.text.api.TextApiFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CustomItem extends Item implements Updatable {

    private static final TextApi textApi = TextApiFactory.create();

    private final ConfigurationSection config;
    private final Plugin plugin;
    private ItemStack currentItem;
    private final int updateInterval;
    private final Map<String, Supplier<String>> replaceSuppliers = new HashMap<>();

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

    public void addReplacement(String placeholder, Supplier<String> replacementSupplier) {
        replaceSuppliers.put(placeholder, replacementSupplier);
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
                            cmd.replace("{player}", player.getName()))
            );
        }
    }

    @Override
    public void update() {
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

        builder.setDisplayName(applyReplacements(config.getString("name")));

        if (config.contains("lore")) {
            List<String> lore = config.getStringList("lore").stream()
                    .map(this::applyReplacements)
                    .collect(Collectors.toList());
            builder.setLore(lore);
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

    private String applyReplacements(String text) {
        return applyReplacements(text, null);
    }

    private String applyReplacements(String text, Player player) {
        if (text == null) return null;

        String result = text;
        for (Map.Entry<String, Supplier<String>> entry : replaceSuppliers.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue().get());
        }

        if (player != null) {
            result = result.replace("{player}", player.getName());
        }

        return result;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}

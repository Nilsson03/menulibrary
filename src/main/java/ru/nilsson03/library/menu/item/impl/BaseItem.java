package ru.nilsson03.library.menu.item.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.bukkit.item.builder.impl.SpigotItemBuilder;

import java.util.List;

public class BaseItem extends Item {
    private final ItemStack itemStack;

    public BaseItem(Material material, String name, List<String> lore) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.itemStack = buildItemStack();
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void update() {}

    private ItemStack buildItemStack() {
        return new SpigotItemBuilder(material)
                .setDisplayName(name)
                .setLore(lore)
                .build();
    }
}

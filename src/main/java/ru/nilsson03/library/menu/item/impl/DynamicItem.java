package ru.nilsson03.library.menu.item.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.nilsson03.library.menu.provider.MenuProvider;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.menu.item.Updatable;

import java.util.function.Supplier;

public class DynamicItem extends Item implements Updatable {
    private final Item innerItem;
    private final Supplier<ItemStack> dynamicSupplier;
    private final int updateInterval;
    private ItemStack currentItem;

    public DynamicItem(MenuProvider menuProvider, Item innerItem, Supplier<ItemStack> dynamicSupplier, int updateInterval) {
        this.innerItem = innerItem;
        this.dynamicSupplier = dynamicSupplier;
        this.updateInterval = updateInterval;
        menuProvider.addNewUpdatableItem(this);
    }

    @Override
    public ItemStack getItemStack() {
        return dynamicSupplier != null ? dynamicSupplier.get() : innerItem.getItemStack();
    }

    @Override
    public void update() {
        currentItem = dynamicSupplier.get();
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent) {
        innerItem.onClick(inventoryClickEvent);
    }

    private ItemStack buildItemStack() {
        ItemStack newItem = dynamicSupplier.get();
        ItemMeta newMeta = newItem.getItemMeta();
        ItemStack item = innerItem.getItemStack();
        item.setItemMeta(newMeta);
        return item;
    }

    public ItemStack getCurrentItem() {
        return currentItem;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }
}

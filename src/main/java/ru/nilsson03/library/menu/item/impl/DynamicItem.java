package ru.nilsson03.library.menu.item.impl;

import org.bukkit.inventory.ItemStack;
import ru.nilsson03.library.menu.provider.MenuProvider;
import ru.nilsson03.library.menu.item.Item;
import ru.nilsson03.library.menu.item.Updatable;

import java.util.function.Supplier;

public class DynamicItem extends Item implements Updatable {
    private final Supplier<ItemStack> dynamicSupplier;
    private final int updateInterval;
    private ItemStack currentItem;

    public DynamicItem(MenuProvider menuProvider, Supplier<ItemStack> dynamicSupplier, int updateInterval) {
        this.dynamicSupplier = dynamicSupplier;
        this.updateInterval = updateInterval;
        menuProvider.addNewUpdatableItem(this);
    }

    @Override
    public ItemStack getItemStack() {
        return dynamicSupplier.get();
    }

    @Override
    public void update() {
        currentItem = dynamicSupplier.get();
    }

    public ItemStack getCurrentItem() {
        return currentItem;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }
}

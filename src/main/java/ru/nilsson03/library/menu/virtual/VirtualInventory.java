package ru.nilsson03.library.menu.virtual;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class VirtualInventory {
    private final String id;
    private final UUID owner;
    private final Inventory inventory;

    public VirtualInventory(String id, UUID owner, Inventory inventory) {
        this.id = id;
        this.owner = owner;
        this.inventory = inventory;
    }

    public String getId() { return id; }
    public UUID getOwner() { return owner; }
    public Inventory getInventory() { return inventory; }

    public ItemStack[] getContents() {
        return inventory.getContents();
    }
}

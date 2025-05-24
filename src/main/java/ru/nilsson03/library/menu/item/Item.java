package ru.nilsson03.library.menu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger;

import java.util.List;
import java.util.function.Consumer;

public abstract class Item {

    protected Material material;
    protected String name;
    protected List<String> lore;

    protected Consumer<Player> clickAction;

    public abstract ItemStack getItemStack();
    public abstract void update();

    public void onClick(InventoryClickEvent event) {
        ConsoleLogger.debug("menulibrary", "======= Item onClick() =======");
        if (event.getWhoClicked() instanceof Player player) {
            if (clickAction != null) {
                clickAction.accept(player);
                ConsoleLogger.debug("menulibrary", "The click action is completed");
            } else {
                ConsoleLogger.debug("menulibrary", "The click action is null");
            }
        }
    }

    public void setClickAction(Consumer<Player> clickAction) {
        this.clickAction = clickAction;
    }

    public Consumer<Player> getClickAction() {
        return clickAction;
    }
}

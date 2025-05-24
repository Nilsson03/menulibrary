package ru.nilsson03.library.menu.item.impl.control;

import ru.nilsson03.library.menu.Menu;
import ru.nilsson03.library.menu.item.Item;

public abstract class ControlItem extends Item {

    private Menu menu;

    public void setMenu(Menu menu) {
        if (this.menu == null)
            this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }
}

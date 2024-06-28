package com.sherdle.webtoapp.drawer.menu;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import com.sherdle.webtoapp.R;


public abstract class SimpleAbstractMenu {
    protected MenuItemCallback callback;
    protected Menu menu;
    protected Map<MenuItem, Action> menuContent = new LinkedHashMap<>();

    
    public MenuItem add(Menu menu, String str, int i, final Action action) {
        MenuItem onMenuItemClickListener = menu.add(R.id.main_group, 0, 0, str).setCheckable(true).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.sherdle.webtoapp.drawer.menu.SimpleAbstractMenu.1
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                SimpleAbstractMenu.this.callback.menuItemClicked(action, menuItem);
                return true;
            }
        });
        if (i != 0) {
            onMenuItemClickListener.setIcon(i);
        }
        this.menuContent.put(onMenuItemClickListener, action);
        return onMenuItemClickListener;
    }


    public Menu getMenu() {
        return this.menu;
    }

    protected MenuItemCallback getMenuItemCallback() {
        return this.callback;
    }

    public Map.Entry<MenuItem, Action> getFirstMenuItem() {
        if (this.menuContent.size() < 1) {
            return null;
        }
        return this.menuContent.entrySet().iterator().next();
    }

    public Set<MenuItem> getMenuItems() {
        return this.menuContent.keySet();
    }
}

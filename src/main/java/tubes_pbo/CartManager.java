package tubes_pbo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CartManager {

    private static CartManager INSTANCE;

    private final IntegerProperty count = new SimpleIntegerProperty(0);
    private final List<CartItem> items = new ArrayList<>();

    private CartManager() {
    }

    public static synchronized CartManager getInstance() {
        if (INSTANCE == null) INSTANCE = new CartManager();
        return INSTANCE;
    }

    public IntegerProperty countProperty() {
        return count;
    }

    public int getCount() {
        return count.get();
    }

    public void addItem(String name, double price) {
        items.add(new CartItem(name, price));
        count.set(items.size());
    }

    public void addItem(String name, double price, int qty) {
        if (qty <= 0) return;
        for (int i = 0; i < qty; i++) {
            items.add(new CartItem(name, price));
        }
        count.set(items.size());
    }

    /** Remove one occurrence of an item with given name. Returns true if removed. */
    public boolean removeOne(String name) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).name.equals(name)) {
                items.remove(i);
                count.set(items.size());
                return true;
            }
        }
        return false;
    }

    /** Set quantity for a product by name. Replaces existing occurrences. */
    public void setQuantity(String name, double price, int qty) {
        // remove all existing
        items.removeIf(it -> it.name.equals(name));
        // add qty times
        for (int i = 0; i < Math.max(0, qty); i++) {
            items.add(new CartItem(name, price));
        }
        count.set(items.size());
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void clear() {
        items.clear();
        count.set(0);
    }

    public static class CartItem {
        public final String name;
        public final double price;

        public CartItem(String name, double price) {
            this.name = name;
            this.price = price;
        }
    }
}

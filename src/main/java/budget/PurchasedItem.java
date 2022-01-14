package budget;

import java.io.Serializable;

public class PurchasedItem implements Serializable {
    private String itemName;
    private String price;
    private Category category;

    public PurchasedItem(String itemName, String price, Category category) {
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }

    public boolean isCategory(Category category) {
        return this.category == category;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPrice() {
        return price;
    }
}

package budget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetBook implements Serializable {
    private double balance;
    private Map<Category, ArrayList<PurchasedItem>> purchasedItems;

    public BudgetBook() {
        balance = 0;
        purchasedItems = new LinkedHashMap<>();
        for (Category category : Category.values()) {
            purchasedItems.put(category, new ArrayList<>());
        }
    }

    public void addIncome(double amount) {
        balance += amount;
    }

    public void showPurchaseList(Category categoryToShow) {
        if (purchasedItems.isEmpty()) {
            System.out.println("Purchase list is empty!");
        } else {
            showCategory(categoryToShow);
        }
    }

    private void showCategory(Category categoryToShow) {
        double totalCost = 0;
        List<PurchasedItem> itemsToShow = purchasedItems.getOrDefault(categoryToShow, new ArrayList<>());
        if (itemsToShow.isEmpty()) {
            System.out.println("Purchase list is empty!");
        } else {
            System.out.println(categoryToShow.toString().charAt(0) +
                    categoryToShow.toString().substring(1).toLowerCase() + ":");
            for (PurchasedItem item : itemsToShow) {
                double cost = Double.parseDouble(item.getPrice());
                System.out.printf(item.getItemName() + " $%.2f\n", cost);
                totalCost += cost;
            }
            System.out.printf("Total sum: $%.2f\n", totalCost);
        }
    }

    public void analyzeByCertainType(Category categoryToAnalyze) {
        double totalCost = 0;
        List<PurchasedItem> itemsToShow = purchasedItems.getOrDefault(categoryToAnalyze, new ArrayList<>());
        if (itemsToShow.isEmpty()) {
            System.out.println("Purchase list is empty!");
        } else {
            System.out.println(categoryToAnalyze.toString().charAt(0) +
                    categoryToAnalyze.toString().substring(1).toLowerCase() + ":");
            List<PurchasedItem> purchasedItemsSorted = new ArrayList<>();
            for (PurchasedItem item : itemsToShow) {
                purchasedItemsSorted.add(item);
                double cost = Double.parseDouble(item.getPrice());
                totalCost += cost;
            }
            purchasedItemsSorted.sort((o1, o2) ->
                    Double.compare(Double.parseDouble(o2.getPrice()), Double.parseDouble(o1.getPrice())));
            purchasedItemsSorted.forEach(purchasedItem -> System.out.printf("%s $%.2f\n",
                    purchasedItem.getItemName(),
                    Double.parseDouble(purchasedItem.getPrice())));
            System.out.printf("Total sum: $%.2f\n", totalCost);
        }
    }

    public void analyzeByType() {
        Set<Category> categories = purchasedItems.keySet();
        categories.remove(Category.ALL);
        Map<Category, Double> mapTotal = new LinkedHashMap<>();
//        double netTotalCost = 0;
        for (Category category : categories) {
            double totalCost;
            List<PurchasedItem> itemsToShow = purchasedItems.get(category);
            totalCost = itemsToShow.stream()
                    .mapToDouble(purchasedItem -> Double.parseDouble(purchasedItem.getPrice()))
                    .sum();
            mapTotal.put(category, totalCost);
//            netTotalCost += totalCost;
        }
//        System.out.println("netTotalCost = " + netTotalCost);
        LinkedHashMap<Category, Double> collectedData = mapTotal.entrySet().stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (aDouble, aDouble2) -> aDouble2, LinkedHashMap::new));

        System.out.println("Types:");
//        collectedData.remove(Category.ALL);
        Set<Category> categoriesSorted = collectedData.keySet();
        for (Category category : categoriesSorted) {
            System.out.printf("%s - $%.2f\n",
                    category.toString().charAt(0) + category.toString().substring(1).toLowerCase(),
                    collectedData.get(category));
        }
//        System.out.printf("Total sum: $%.2f\n", netTotalCost);
        //(o1, o2) -> o1.getValue() > o2.getValue() ? (int) (double) o1.getValue() :
        //                        (int) (double) o2.getValue()
    }

    public boolean isPurchaseListEmpty() {
        return purchasedItems.get(Category.ALL).isEmpty();
    }

    public void addPurchase(String itemName, String price, Category category, boolean subtractBalance) {
        if (subtractBalance) {
            balance -= Double.parseDouble(price);
        }
        PurchasedItem itemPurchased = new PurchasedItem(itemName, price, category);
        ArrayList<PurchasedItem> listToUpdate = purchasedItems.getOrDefault(category, new ArrayList<>());
        listToUpdate.add(itemPurchased);
        purchasedItems.put(category, listToUpdate);
        ArrayList<PurchasedItem> allList = purchasedItems.get(Category.ALL);
        allList.add(itemPurchased);
        purchasedItems.put(Category.ALL, allList);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void writeToFile(File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        StringBuilder outputString = new StringBuilder();
        outputString.append("Balance $").append(balance).append("\n");
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length - 1; i++) {
            outputString.append(categories[i].toString()).append(": ").append("\n");
            purchasedItems.get(categories[i]).forEach(purchasedItem ->
                    outputString.append(purchasedItem.getItemName())
                            .append(" $")
                            .append(purchasedItem.getPrice())
                            .append("\n"));
//            outputString.append("\n");
        }
        byte[] bytesOutput = String.valueOf(outputString).getBytes();
        outputStream.write(bytesOutput);
    }

}

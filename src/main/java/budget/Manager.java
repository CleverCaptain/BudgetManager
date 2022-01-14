package budget;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manager {
    public static void main(String[] args) {
        try (final Scanner kb = new Scanner(System.in)) {
            boolean isRunning = true;
            String fileName = "BudgetManager\\purchases.txt";
            File file = new File(fileName);
            BudgetBook budgetBook = new BudgetBook();
            while (isRunning) {
                System.out.println("""
                      Choose your action:
                      1) Add income
                      2) Add purchase
                      3) Show list of purchases
                      4) Balance
                      5) Save
                      6) Load
                      7) Analyze (Sort)
                      0) Exit""");

                int choice = Integer.parseInt(kb.nextLine());
                System.out.println();
                switch (choice) {
                    case 1 -> {
                        System.out.println("Enter income:");
                        double amountToAdd = Double.parseDouble(kb.nextLine());
                        budgetBook.addIncome(amountToAdd);
                        System.out.println("Income was added!");
                    }
                    case 2 -> {
                        boolean isAddingItems = true;
                        while (isAddingItems) {
                            System.out.println("""
                                  Choose the type of purchase
                                  1) Food
                                  2) Clothes
                                  3) Entertainment
                                  4) Other
                                  5) Back""");
                            int purchaseChoice = Integer.parseInt(kb.nextLine());
                            Category category = null;
                            switch (purchaseChoice) {
                                case 1 -> category = Category.FOOD;
                                case 2 -> category = Category.CLOTHES;
                                case 3 -> category = Category.ENTERTAINMENT;
                                case 4 -> category = Category.OTHER;
                                case 5 -> isAddingItems = false;
                            }
                            if (isAddingItems) {
                                System.out.println();
                                System.out.println("Enter purchase name:");
                                String purchaseName = kb.nextLine().trim();
                                System.out.println("Enter its price:");
                                String price = kb.nextLine();
                                budgetBook.addPurchase(purchaseName, price, category, true);
                                System.out.println("Purchase was added!");
                                System.out.println();
                            }
                        }
                    }
                    case 3 -> {
                        boolean isShowing;
                        if (budgetBook.isPurchaseListEmpty()) {
                            isShowing = false;
                            System.out.println("Purchase list is empty");
                        } else {
                            isShowing = true;
                        }
                        while (isShowing) {
                            System.out.println("""
                                  Choose the type of purchases
                                  1) Food
                                  2) Clothes
                                  3) Entertainment
                                  4) Other
                                  5) All
                                  6) Back""");
                            int showChoice = Integer.parseInt(kb.nextLine());
                            Category categoryToShow = null;
                            switch (showChoice) {
                                case 1 -> categoryToShow = Category.FOOD;
                                case 2 -> categoryToShow = Category.CLOTHES;
                                case 3 -> categoryToShow = Category.ENTERTAINMENT;
                                case 4 -> categoryToShow = Category.OTHER;
                                case 5 -> categoryToShow = Category.ALL;
                                case 6 -> isShowing = false;
                            }
                            if (isShowing) {
                                System.out.println();
                                budgetBook.showPurchaseList(categoryToShow);
                                System.out.println();
                            }
                        }
                    }
                    case 4 -> System.out.printf("Balance: $%.2f\n", budgetBook.getBalance());
                    case 5 -> {
                        System.out.println("Purchases were saved!");
                        if (!file.exists()) {
                            boolean isCreated = file.createNewFile();
                            System.out.println(isCreated);
                        }
                        System.out.println(file.exists());
                        budgetBook.writeToFile(file);
                    }
                    case 6 -> {
                        System.out.println("Purchases were loaded!");
                        if (!file.exists()) {
                            System.out.println("File does not exists!");
                            break;
                        }
                        budgetBook = readFromFile(file);
                    }
                    case 7 -> {
                        boolean isAnalysing = true;
                        while (isAnalysing) {
                            System.out.println("""
                                  How do you want to sort?
                                  1) Sort all purchases
                                  2) Sort by type
                                  3) Sort certain type
                                  4) Back""");
                            int analysisChoice = Integer.parseInt(kb.nextLine());
                            switch (analysisChoice) {
                                case 1 -> {
                                    System.out.println();
                                    budgetBook.analyzeByCertainType(Category.ALL);
                                    System.out.println();
                                }
                                case 2 -> {
                                    System.out.println();
                                    budgetBook.analyzeByType();
                                    System.out.println();
                                }
                                case 3 -> {
                                    System.out.println();
                                    System.out.println("""
                                          Choose the type of purchase
                                          1) Food
                                          2) Clothes
                                          3) Entertainment
                                          4) Other""");
                                    int analyzeCategoryChoice = Integer.parseInt(kb.nextLine());
                                    Category categoryToAnalyze = switch (analyzeCategoryChoice) {
                                        case 1 -> Category.FOOD;
                                        case 2 -> Category.CLOTHES;
                                        case 3 -> Category.ENTERTAINMENT;
                                        case 4 -> Category.OTHER;
                                        default -> null;
                                    };
                                    System.out.println();
                                    budgetBook.analyzeByCertainType(categoryToAnalyze);
                                    System.out.println();
                                }
                                case 4 -> isAnalysing = false;
                            }
                        }
                    }
                    case 0 -> {
                        System.out.println("Bye!");
                        isRunning = false;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + choice);
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BudgetBook readFromFile(File file) throws IOException {
        BudgetBook budgetBook = new BudgetBook();
        Scanner fileReader = new Scanner(file);
        Category category = null;
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine().trim();
            String[] data = new String[2];
            if (line.contains("$")) {
                Pattern pattern = Pattern.compile("\\s?\\$[\\d.]+$");
                Matcher matcher = pattern.matcher(line);
                boolean matcherFound = matcher.find();
                int start = matcher.toMatchResult().start();
                data[0] = line.substring(0, start);
                data[1] = line.substring(start + 2);
//                data = line.split("\\s?\\$[\\d.]+$");
            } else {
                data[0] = line.substring(0, line.length() - 1);
            }
            switch (data[0]) {
                case "Balance" -> budgetBook.setBalance(Double.parseDouble(data[1]));
                case "FOOD" -> category = Category.FOOD;
                case "CLOTHES" -> category = Category.CLOTHES;
                case "ENTERTAINMENT" -> category = Category.ENTERTAINMENT;
                case "OTHER" -> category = Category.OTHER;
                default -> {
                    System.out.println("data[0] = " + data[0]);
                    System.out.println("data[1] = " + data[1]);
                    System.out.println("category = " + category);
                    budgetBook.addPurchase(data[0], data[1], category, false);
                }
            }
        }
        return budgetBook;
    }
}

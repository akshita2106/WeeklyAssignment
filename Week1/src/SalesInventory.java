import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SalesInventory {

    // product -> stock
    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // product -> waiting list
    private ConcurrentHashMap<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();

    // Add product to inventory
    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new ConcurrentLinkedQueue<>());
    }

    // Check stock availability
    public void checkStock(String productId) {

        if (!inventory.containsKey(productId)) {
            System.out.println("Product not found");
            return;
        }

        int stock = inventory.get(productId).get();
        System.out.println("checkStock(\"" + productId + "\") → " + stock + " units available");
    }

    // Purchase item
    public synchronized void purchaseItem(String productId, int userId) {

        if (!inventory.containsKey(productId)) {
            System.out.println("Product not found");
            return;
        }

        AtomicInteger stock = inventory.get(productId);

        if (stock.get() > 0) {

            int remaining = stock.decrementAndGet();

            System.out.println("purchaseItem(\"" + productId + "\", userId=" + userId +
                    ") → Success, " + remaining + " units remaining");

        } else {

            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);

            System.out.println("purchaseItem(\"" + productId + "\", userId=" + userId +
                    ") → Added to waiting list, position #" + queue.size());
        }
    }

    public static void main(String[] args) {

        SalesInventory manager = new SalesInventory();

        manager.addProduct("IPHONE15_256GB", 100);

        manager.checkStock("IPHONE15_256GB");

        manager.purchaseItem("IPHONE15_256GB", 12345);
        manager.purchaseItem("IPHONE15_256GB", 67890);

        // simulate stock finishing
        for (int i = 0; i < 98; i++) {
            manager.purchaseItem("IPHONE15_256GB", 10000 + i);
        }

        // stock finished
        manager.purchaseItem("IPHONE15_256GB", 99999);
    }
}
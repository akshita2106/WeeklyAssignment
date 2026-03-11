import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsernameAvailability {

    // Stores existing usernames
    private Set<String> usernames;

    // Tracks how many times a username was attempted
    private ConcurrentHashMap<String, AtomicInteger> attemptCount;

    public UsernameAvailability() {
        usernames = ConcurrentHashMap.newKeySet();
        attemptCount = new ConcurrentHashMap<>();

        // Sample existing users
        usernames.add("john_doe");
        usernames.add("admin");
        usernames.add("alex");
    }

    // Check availability in O(1)
    public boolean checkAvailability(String username) {

        attemptCount.putIfAbsent(username, new AtomicInteger(0));
        attemptCount.get(username).incrementAndGet();

        return !usernames.contains(username);
    }

    // Suggest alternatives if username is taken
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        if (!usernames.contains(username)) {
            suggestions.add(username);
            return suggestions;
        }

        int counter = 1;

        while (suggestions.size() < 5) {

            String suggestion = username + counter;

            if (!usernames.contains(suggestion)) {
                suggestions.add(suggestion);
            }

            counter++;
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptCount.entrySet()) {

            if (entry.getValue().get() > max) {
                max = entry.getValue().get();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }

    // Register a username
    public void registerUser(String username) {
        usernames.add(username);
    }

    public static void main(String[] args) {

        UsernameAvailability system = new UsernameAvailability();

        System.out.println("checkAvailability(\"john_doe\") → "
                + system.checkAvailability("john_doe"));

        System.out.println("checkAvailability(\"jane_smith\") → "
                + system.checkAvailability("jane_smith"));

        System.out.println("suggestAlternatives(\"john_doe\") → "
                + system.suggestAlternatives("john_doe"));

        // simulate repeated attempts
        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("admin");

        System.out.println("getMostAttempted() → "
                + system.getMostAttempted());
    }
}
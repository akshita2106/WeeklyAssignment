import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queries = new HashMap<>();
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> frequencyMap = new HashMap<>();

    // Insert query into Trie
    public void addQuery(String query) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.put(query, frequencyMap.get(query));
        }
    }

    // Search prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(node.queries.entrySet());

        List<String> result = new ArrayList<>();

        int k = 10;

        while (!pq.isEmpty() && k-- > 0) {
            Map.Entry<String, Integer> entry = pq.poll();
            result.add(entry.getKey() + " (" + entry.getValue() + ")");
        }

        return result;
    }

    // Update frequency when search happens
    public void updateFrequency(String query) {
        addQuery(query);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");

        System.out.println("Search results for 'jav':");

        List<String> results = system.search("jav");

        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }

        System.out.println("\nUpdating frequency...");
        system.updateFrequency("java 21 features");

        System.out.println("\nSearch results for 'jav' after update:");

        results = system.search("jav");

        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }
    }
}
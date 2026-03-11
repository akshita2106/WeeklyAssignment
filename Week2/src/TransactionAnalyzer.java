import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;

    public Transaction(int id, int amount, String merchant, String account) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
    }
}

public class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Two-Sum
    public void findTwoSum(int target) {

        Map<Integer, Transaction> map = new HashMap<>();

        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                result.add("(id:" + other.id + ", id:" + t.id + ")");
            }

            map.put(t.amount, t);
        }

        System.out.println("findTwoSum(target=" + target + ") → " + result);
    }

    // Duplicate detection
    public void detectDuplicates() {

        Map<String, List<String>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t.account);
        }

        for (String key : map.keySet()) {

            List<String> accounts = map.get(key);

            if (accounts.size() > 1) {

                String[] parts = key.split("-");

                System.out.println(
                        "detectDuplicates() → {amount:" + parts[0] +
                                ", merchant:\"" + parts[1] +
                                "\", accounts:" + accounts + "}"
                );
            }
        }
    }

    // K-Sum
    public void findKSum(int k, int target) {
        kSumHelper(0, k, target, new ArrayList<>());
    }

    private void kSumHelper(int start, int k, int target, List<Transaction> path) {

        if (k == 0 && target == 0) {

            System.out.print("findKSum(k=" + path.size() +
                    ", target=" + (path.stream().mapToInt(t -> t.amount).sum()) +
                    ") → [(");

            for (int i = 0; i < path.size(); i++) {

                System.out.print("id:" + path.get(i).id);

                if (i < path.size() - 1)
                    System.out.print(", ");
            }

            System.out.println(")]");

            return;
        }

        if (k <= 0)
            return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            path.add(t);

            kSumHelper(i + 1, k - 1, target - t.amount, path);

            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "Store A", "acc1"));
        analyzer.addTransaction(new Transaction(2, 300, "Store B", "acc2"));
        analyzer.addTransaction(new Transaction(3, 200, "Store C", "acc3"));
        analyzer.addTransaction(new Transaction(4, 500, "Store A", "acc4"));

        analyzer.findTwoSum(500);

        analyzer.detectDuplicates();

        analyzer.findKSum(3, 1000);
    }
}
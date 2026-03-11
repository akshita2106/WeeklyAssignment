import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000L;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private final int MAX_CACHE_SIZE = 5;

    private LinkedHashMap<String, DNSEntry> cache =
            new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };

    private int hits = 0;
    private int misses = 0;

    // Simulated upstream DNS lookup
    private String queryUpstream(String domain) {
        try {
            Thread.sleep(100); // simulate 100ms DNS lookup
        } catch (InterruptedException e) {}
        return "172.217.14." + new Random().nextInt(255);
    }

    // Resolve domain
    public String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long time = System.nanoTime() - start;
                System.out.println("resolve(\"" + domain + "\") → Cache HIT → "
                        + entry.ipAddress + " (retrieved in " + time / 1_000_000.0 + " ms)");
                return entry.ipAddress;
            } else {
                System.out.println("resolve(\"" + domain + "\") → Cache EXPIRED");
                cache.remove(domain);
            }
        }

        misses++;
        String ip = queryUpstream(domain);

        DNSEntry entry = new DNSEntry(domain, ip, 300);
        cache.put(domain, entry);

        System.out.println("resolve(\"" + domain + "\") → Cache MISS → Query upstream → "
                + ip + " (TTL: 300s)");

        return ip;
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("getCacheStats() → Hit Rate: "
                + String.format("%.2f", hitRate) + "%");
    }

    public static void main(String[] args) throws Exception {

        DNSCache cache = new DNSCache();

        cache.resolve("google.com");
        cache.resolve("google.com");

        Thread.sleep(2000);

        cache.resolve("github.com");
        cache.resolve("google.com");

        cache.getCacheStats();
    }
}
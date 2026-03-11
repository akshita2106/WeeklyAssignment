import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {

    int tokens;
    int maxTokens;
    double refillRate; // tokens per second
    long lastRefillTime;

    public TokenBucket(int maxTokens, int refillPeriodSeconds) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillRate = (double) maxTokens / refillPeriodSeconds;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // refill tokens
    private void refill() {

        long now = System.currentTimeMillis();
        double seconds = (now - lastRefillTime) / 1000.0;

        int tokensToAdd = (int) (seconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    // try consuming token
    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return tokens;
    }
}

public class DistributedRateLimiter {

    private static final int MAX_REQUESTS = 1000;
    private static final int WINDOW_SECONDS = 3600;

    private ConcurrentHashMap<String, TokenBucket> clientBuckets =
            new ConcurrentHashMap<>();

    public boolean checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(
                clientId,
                new TokenBucket(MAX_REQUESTS, WINDOW_SECONDS)
        );

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {

            System.out.println("checkRateLimit(clientId=\"" + clientId + "\") → Allowed (" +
                    bucket.getRemainingTokens() + " requests remaining)");

            return true;
        } else {

            System.out.println("checkRateLimit(clientId=\"" + clientId + "\") → Denied (0 requests remaining)");

            return false;
        }
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("No requests yet for " + clientId);
            return;
        }

        int used = MAX_REQUESTS - bucket.getRemainingTokens();

        System.out.println("getRateLimitStatus(\"" + clientId + "\") → {used: "
                + used + ", limit: " + MAX_REQUESTS + "}");
    }

    public static void main(String[] args) {

        DistributedRateLimiter limiter = new DistributedRateLimiter();

        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");

        limiter.getRateLimitStatus("abc123");
    }
}
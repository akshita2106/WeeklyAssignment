import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    public PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {

    // page -> total views
    private Map<String, Integer> pageViews = new HashMap<>();

    // page -> unique visitors
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();

    // source -> count
    private Map<String, Integer> trafficSources = new HashMap<>();

    // Process incoming event
    public void processEvent(PageEvent event) {

        // update page views
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        // update unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // update traffic source
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get Top 10 pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();

        for (int i = 0; i < 10 && !pq.isEmpty(); i++) {
            topPages.add(pq.poll());
        }

        return topPages;
    }

    // Dashboard display
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME ANALYTICS DASHBOARD =====");

        System.out.println("\nTop Pages:");

        int rank = 1;

        for (Map.Entry<String, Integer> entry : getTopPages()) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" + unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (String source : trafficSources.keySet()) {
            System.out.println(source + " → " + trafficSources.get(source));
        }
    }

    public static void main(String[] args) {

        RealTimeAnalyticsDashboard analytics = new RealTimeAnalyticsDashboard();

        // simulate incoming events
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_999", "google"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_111", "direct"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_789", "google"));

        analytics.getDashboard();
    }
}
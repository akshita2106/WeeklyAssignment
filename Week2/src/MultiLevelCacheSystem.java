import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class LRUCache<K,V> extends LinkedHashMap<K,V> {

    private int capacity;

    public LRUCache(int capacity) {
        super(capacity,0.75f,true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > capacity;
    }
}

public class MultiLevelCacheSystem {

    // L1 memory cache
    private LRUCache<String, VideoData> L1 = new LRUCache<>(10000);

    // L2 SSD cache
    private LRUCache<String, VideoData> L2 = new LRUCache<>(100000);

    // Simulated database
    private Map<String, VideoData> L3 = new HashMap<>();

    private int l1Hits=0,l2Hits=0,l3Hits=0,totalRequests=0;

    public MultiLevelCacheSystem() {

        // preload database
        L3.put("video_123", new VideoData("video_123","Video Content A"));
        L3.put("video_999", new VideoData("video_999","Video Content B"));
    }

    public VideoData getVideo(String videoId) {

        totalRequests++;

        long start = System.nanoTime();

        // L1 check
        if(L1.containsKey(videoId)) {

            l1Hits++;

            System.out.println("→ L1 Cache HIT (0.5ms)");

            return L1.get(videoId);
        }

        System.out.println("→ L1 Cache MISS (0.5ms)");

        // L2 check
        if(L2.containsKey(videoId)) {

            l2Hits++;

            VideoData data = L2.get(videoId);

            L1.put(videoId,data);

            System.out.println("→ L2 Cache HIT (5ms)");
            System.out.println("→ Promoted to L1");

            return data;
        }

        System.out.println("→ L2 Cache MISS");

        // L3 database
        if(L3.containsKey(videoId)) {

            l3Hits++;

            VideoData data = L3.get(videoId);

            L2.put(videoId,data);

            System.out.println("→ L3 Database HIT (150ms)");
            System.out.println("→ Added to L2");

            return data;
        }

        System.out.println("Video not found");

        return null;
    }

    public void getStatistics() {

        double l1Rate = (double)l1Hits/totalRequests*100;
        double l2Rate = (double)l2Hits/totalRequests*100;
        double l3Rate = (double)l3Hits/totalRequests*100;

        System.out.println("\ngetStatistics() →");

        System.out.println("L1: Hit Rate "+String.format("%.0f",l1Rate)+"%");
        System.out.println("L2: Hit Rate "+String.format("%.0f",l2Rate)+"%");
        System.out.println("L3: Hit Rate "+String.format("%.0f",l3Rate)+"%");
        System.out.println("Overall Requests: "+totalRequests);
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        System.out.println("getVideo(\"video_123\")");

        cache.getVideo("video_123");

        System.out.println("\ngetVideo(\"video_123\") [second request]");

        cache.getVideo("video_123");

        System.out.println("\ngetVideo(\"video_999\")");

        cache.getVideo("video_999");

        cache.getStatistics();
    }
}

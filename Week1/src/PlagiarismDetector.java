import java.util.*;

public class PlagiarismDetector {

    // n-gram index: ngram -> list of document names
    private Map<String, List<String>> ngramIndex = new HashMap<>();

    private int N = 3; // size of n-gram

    // Break document into n-grams
    public List<String> extractNGrams(String text) {

        List<String> ngrams = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Store document n-grams in hash table
    public void addDocument(String docName, String text) {

        List<String> grams = extractNGrams(text);

        for (String gram : grams) {

            ngramIndex.putIfAbsent(gram, new ArrayList<>());

            if (!ngramIndex.get(gram).contains(docName)) {
                ngramIndex.get(gram).add(docName);
            }
        }
    }

    // Analyze a new document
    public void analyzeDocument(String docName, String text) {

        List<String> grams = extractNGrams(text);

        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : grams) {

            if (ngramIndex.containsKey(gram)) {

                for (String existingDoc : ngramIndex.get(gram)) {

                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        System.out.println("analyzeDocument(\"" + docName + "\")");
        System.out.println("→ Extracted " + grams.size() + " n-grams");

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);

            double similarity = (matches * 100.0) / grams.size();

            System.out.println("→ Found " + matches +
                    " matching n-grams with \"" + doc + "\"");

            System.out.printf("→ Similarity: %.1f%% ", similarity);

            if (similarity > 50) {
                System.out.println("(PLAGIARISM DETECTED)");
            } else if (similarity > 10) {
                System.out.println("(suspicious)");
            } else {
                System.out.println("(low similarity)");
            }
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        // existing essays
        detector.addDocument("essay_089.txt",
                "machine learning is used in many applications such as healthcare finance and automation");

        detector.addDocument("essay_092.txt",
                "machine learning is used in many applications such as healthcare finance and automation with data analysis");

        // new essay
        String newEssay = "machine learning is used in many applications such as healthcare finance and automation";

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}
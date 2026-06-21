package com.apex.hotelreservation.service;

import com.apex.hotelreservation.model.KnowledgeDocument;
import com.apex.hotelreservation.repository.KnowledgeDocumentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RAGService {

    @Autowired
    private KnowledgeDocumentRepository docRepository;

    @Value("${ai.gemini.api_key}")
    private String geminiApiKey;

    @PostConstruct
    public void seedKnowledge() {
        if (docRepository.count() == 0) {
            docRepository.save(new KnowledgeDocument("resort", "Resort Rates and Stays",
                    "Luxury Retreat features 6 different stay properties: " +
                    "1. Garden Villa Resort: 122 rooms, rate is $180 per night, featuring 3 Presidential Suites, 23 Junior Suites, 96 Designer Rooms, 48 Gardens, 20 Swimming Pools. " +
                    "2. Luxury Resort: 50 rooms, rate is $250 per night, featuring Presidential Suite, 4 Designer Suites, 44 Designer Rooms. " +
                    "3. Camp Luxury Retreat: 30 glamping tents, rate is $350 per night, India's 1st glamping tent resort established in 2010. " +
                    "4. Adventure Resort: 44 rooms, rate is $220 per night, features rooms overlooking the extreme adventure park. " +
                    "5. Enclave Villa Resort: 40 villas, rate is $290 per night, features private lawns and select villas with private swimming pools. " +
                    "6. D.A.T.A. Resort (D.A.T.A. Military Escape): 27 military-themed glamping tents, rate is $390 per night, world's 1st luxury military themed resort including a Presidential Suite."));

            docRepository.save(new KnowledgeDocument("gastronomy", "Restaurants and Cuisines",
                    "Luxury Retreat offers 8 award-winning theme-based dining establishments: " +
                    "- Café 24: India's first 24-hour fine dining restaurant offering multi-cuisine menus. " +
                    "- Villa Bistro: Al fresco Italian restaurant and bar featuring handcrafted cocktails. " +
                    "- Parsi Dhaba: India's largest Parsi restaurant, blending traditional Parsi delicacies with Punjabi Dhaba cuisine. " +
                    "- PNF: Premium lounge and restaurant serving Malvani Indian, Asian, and regional curries. " +
                    "- Crème Luxury Retreat: Premium pure vegetarian restaurant in an open-air setting. " +
                    "- Sports Bar: Casual restaurant serving burgers, sandwiches, pizza, pasta, and Mexican snacks. " +
                    "- Salaam Manekshaw: Multi-cuisine fine dining in a jungle theme, dedicated to Field Marshal Sam Manekshaw. " +
                    "- Sky Garden: Suspended dining lounge hanging 150 feet in the air with panoramic views."));

            docRepository.save(new KnowledgeDocument("adventure", "Adventure Park Activities",
                    "Luxury Retreat is home to India's largest extreme adventure park featuring over 50 activities. " +
                    "Highlights include: India's only Swoop Swing (100 ft), India's longest zip-line Flying Fox (1250 ft), and the Luxury Bungee Jump (150 ft). " +
                    "Adventure tickets and day passes are available at the resort counter."));

            docRepository.save(new KnowledgeDocument("spa", "24-Hour Wellness Spa",
                    "The Luxury Spa is open 24 hours a day, offering deep tissue massage, detoxification therapies, " +
                    "holistic wellness treatments, and newly designed couples treatment suites. All bookings include access " +
                    "to steam, sauna, and relaxation lounges."));

            docRepository.save(new KnowledgeDocument("policy", "Booking Policies and Information",
                    "Standard check-in time at Luxury Retreat is 2:00 PM and check-out is 11:00 AM. " +
                    "We offer free cancellation up to 48 hours prior to arrival; bookings cancelled within 48 hours incur a 100% room charge. " +
                    "The resort is entirely pet-friendly and features pet grooming amenities."));
        }
    }

    public List<KnowledgeDocument> search(String query, int limit) {
        List<KnowledgeDocument> allDocs = docRepository.findAll();
        if (allDocs.isEmpty()) return new ArrayList<>();

        // Basic TF-IDF-like keyword overlap search
        Set<String> queryWords = tokenize(query);
        if (queryWords.isEmpty()) return allDocs.subList(0, Math.min(limit, allDocs.size()));

        Map<KnowledgeDocument, Double> scoredDocs = new HashMap<>();
        for (KnowledgeDocument doc : allDocs) {
            Set<String> docWords = tokenize(doc.getContent() + " " + doc.getTitle() + " " + doc.getCategory());
            long intersectCount = queryWords.stream().filter(docWords::contains).count();
            // Score = Intersection / Total Query Words
            double score = (double) intersectCount / queryWords.size();
            scoredDocs.put(doc, score);
        }

        return scoredDocs.entrySet().stream()
                .filter(e -> e.getValue() > 0.0) // Only keep documents that match at least one word
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public String generateResponse(String query, List<KnowledgeDocument> contextDocs) {
        // Build prompt context
        StringBuilder context = new StringBuilder();
        for (KnowledgeDocument doc : contextDocs) {
            context.append("[").append(doc.getTitle()).append("]: ").append(doc.getContent()).append("\n");
        }

        String prompt = "You are the AI Concierge for 'Luxury Retreat' (an ultra-luxury experiential resort in India, formerly known as Della). " +
                "Ground your answers STRICTLY in the following facts:\n\n" +
                context.toString() + "\n" +
                "User Question: " + query + "\n" +
                "Provide a helpful, professional, and detailed response. If the query cannot be answered using the facts above, say: " +
                "\"I'm happy to help, but that specific detail isn't in my system database. Let me connect you with a guest relationship representative.\"";

        // Check if a real Gemini API Key is configured (not mock_gemini_api_key)
        if (geminiApiKey != null && !geminiApiKey.equals("mock_gemini_api_key") && !geminiApiKey.isEmpty()) {
            try {
                return callGeminiAPI(prompt);
            } catch (Exception e) {
                System.err.println("Gemini API call failed, falling back to local: " + e.getMessage());
            }
        }

        // Local RAG Template Engine fallback
        return generateLocalRAGResponse(query, contextDocs);
    }

    private String callGeminiAPI(String promptText) throws Exception {
        String urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Escape JSON string characters
        String escapedPrompt = promptText.replace("\\", "\\\\")
                                         .replace("\"", "\\\"")
                                         .replace("\n", "\\n")
                                         .replace("\r", "\\r");

        String jsonInput = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapedPrompt + "\"}]}]}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        // Basic JSON regex extraction for Gemini response
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(response.toString());
        if (matcher.find()) {
            // Unescape raw text characters
            return matcher.group(1).replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
        }

        return "Gemini API parsed answer successfully, but content parts were empty.";
    }

    private String generateLocalRAGResponse(String query, List<KnowledgeDocument> contextDocs) {
        if (contextDocs.isEmpty()) {
            return "Welcome to Luxury Retreat! I'm your AI Concierge. I can help you with room pricing, restaurant reservations, spa bookings, or adventure park passes. How can I help you today?";
        }

        // Draft standard local template replies based on context match
        KnowledgeDocument bestDoc = contextDocs.get(0);
        String category = bestDoc.getCategory();

        if (category.equals("resort")) {
            return "Luxury Retreat features world-class stays. Here is what I found in our room inventory database: \n\n" +
                    bestDoc.getContent() + "\n\n" +
                    "To book a room, you can click the 'Book Now' button at the top to complete a reservation instantly!";
        } else if (category.equals("gastronomy")) {
            return "Our gastronomy selection features 8 signature theme-based restaurants:\n\n" +
                    bestDoc.getContent() + "\n\n" +
                    "Would you like to book a table at one of these fine dining spots?";
        } else if (category.equals("adventure")) {
            return "If you are looking for thrill and activities, Luxury Retreat has you covered:\n\n" +
                    bestDoc.getContent() + "\n\n" +
                    "You can purchase passes at the registration counter upon arrival.";
        } else if (category.equals("spa")) {
            return "For relaxation, our 24-hour spa is the perfect spot:\n\n" +
                    bestDoc.getContent() + "\n\n" +
                    "Let me know if you would like me to pre-book a spa treatment for your stay!";
        } else {
            return "Regarding your query: \n\n" + bestDoc.getContent();
        }
    }

    private Set<String> tokenize(String text) {
        if (text == null) return new HashSet<>();
        String[] words = text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .split("\\s+");
        Set<String> set = new HashSet<>();
        for (String w : words) {
            if (w.length() > 2 && !isStopWord(w)) {
                set.add(w);
            }
        }
        return set;
    }

    private boolean isStopWord(String word) {
        String[] stops = {"the", "and", "a", "of", "to", "in", "is", "that", "this", "it", "for", "on", "with", "as", "at", "by", "an", "be"};
        for (String stop : stops) {
            if (stop.equals(word)) return true;
        }
        return false;
    }
}

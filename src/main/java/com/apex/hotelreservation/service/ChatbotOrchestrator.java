package com.apex.hotelreservation.service;

import com.apex.hotelreservation.model.Room;
import com.apex.hotelreservation.model.KnowledgeDocument;
import com.apex.hotelreservation.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatbotOrchestrator {

    @Autowired
    private RAGService ragService;

    @Autowired
    private RoomRepository roomRepository;

    public Map<String, Object> processMessage(String message, String sessionId) {
        Map<String, Object> trace = new HashMap<>();
        List<String> logs = new ArrayList<>();
        logs.add("[Orchestrator] Received user message: '" + message + "'");

        // Intent detection
        String intent = "FAQ";
        if (message.toLowerCase().contains("room") || message.toLowerCase().contains("stay") || message.toLowerCase().contains("price") || message.toLowerCase().contains("rate")) {
            intent = "ROOM_INQUIRY";
        } else if (message.toLowerCase().contains("eat") || message.toLowerCase().contains("food") || message.toLowerCase().contains("restaurant") || message.toLowerCase().contains("parsi") || message.toLowerCase().contains("dhaba")) {
            intent = "GASTRONOMY_INQUIRY";
        } else if (message.toLowerCase().contains("bungee") || message.toLowerCase().contains("adventure") || message.toLowerCase().contains("zip")) {
            intent = "ADVENTURE_INQUIRY";
        }

        logs.add("[IntentDetector] Identified Intent: " + intent);

        // Tool Calling simulation
        String toolCallResult = null;
        if (intent.equals("ROOM_INQUIRY")) {
            logs.add("[ToolAgent] Executing tool: roomRepository.findAll()...");
            List<Room> rooms = roomRepository.findAll();
            double minPrice = rooms.stream().mapToDouble(Room::getPricePerNight).min().orElse(100.0);
            toolCallResult = "Current lowest active rate in the database is $" + minPrice + " per night.";
            logs.add("[ToolAgent] Tool Output: " + toolCallResult);
        }

        // RAG search
        logs.add("[RAGService] Running TF-IDF match search on knowledge repository...");
        List<KnowledgeDocument> contextDocs = ragService.search(message, 3);
        
        StringBuilder retrievedDocsLog = new StringBuilder("[RAGService] Retrieved context chunks: ");
        if (contextDocs.isEmpty()) {
            retrievedDocsLog.append("None. Using default concierge model.");
        } else {
            for (KnowledgeDocument d : contextDocs) {
                retrievedDocsLog.append("[").append(d.getTitle()).append("] ");
            }
        }
        logs.add(retrievedDocsLog.toString());

        // Generation
        logs.add("[Generator] Grounding response and calling LLM engine...");
        String reply = ragService.generateResponse(message, contextDocs);
        
        if (toolCallResult != null) {
            reply = reply + "\n\n(System live check: " + toolCallResult + ")";
        }

        logs.add("[Orchestrator] Concierge conversation step complete.");

        trace.put("reply", reply);
        trace.put("intent", intent);
        trace.put("logs", logs);
        trace.put("docs", contextDocs.stream().map(KnowledgeDocument::getTitle).toArray());

        return trace;
    }
}

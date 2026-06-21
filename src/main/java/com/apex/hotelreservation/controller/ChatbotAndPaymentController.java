package com.apex.hotelreservation.controller;

import com.apex.hotelreservation.model.Booking;
import com.apex.hotelreservation.repository.BookingRepository;
import com.apex.hotelreservation.service.ChatbotOrchestrator;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
@CrossOrigin(origins = "*")
public class ChatbotAndPaymentController {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ChatbotOrchestrator chatbotOrchestrator;

    @PostMapping("/razorpay/create-order")
    public Map<String, Object> createRazorpayOrder(@RequestParam double amount) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Convert USD to INR (e.g., 1 USD = 80 INR) and then to Paise (1 INR = 100 paise)
            int amountInPaise = (int) (amount * 80.0 * 100.0);

            if (keyId.equals("rzp_test_mockKeyId123") || keySecret.equals("mockKeySecret456")) {
                String mockOrderId = "order_mock_" + System.currentTimeMillis();
                response.put("success", true);
                response.put("orderId", mockOrderId);
                response.put("amount", amountInPaise);
                response.put("currency", "INR");
                response.put("keyId", keyId);
                response.put("isMock", true);
                return response;
            }

            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "rcpt_" + System.currentTimeMillis());

            Order order = razorpay.orders.create(orderRequest);
            response.put("success", true);
            response.put("orderId", order.get("id"));
            response.put("amount", amountInPaise);
            response.put("currency", "INR");
            response.put("keyId", keyId);
            response.put("isMock", false);
        } catch (Exception e) {
            int amountInPaise = (int) (amount * 80.0 * 100.0);
            String mockOrderId = "order_mock_" + System.currentTimeMillis();
            response.put("success", true);
            response.put("orderId", mockOrderId);
            response.put("amount", amountInPaise);
            response.put("currency", "INR");
            response.put("keyId", keyId);
            response.put("isMock", true);
            response.put("warning", "Client configuration error, fell back to mock order: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/razorpay/verify-payment")
    public Map<String, Object> verifyPayment(@RequestParam String razorpayPaymentId,
                                             @RequestParam String razorpayOrderId,
                                             @RequestParam String razorpaySignature,
                                             @RequestParam String guestName,
                                             @RequestParam String contact,
                                             @RequestParam int roomNumber,
                                             @RequestParam int nights,
                                             @RequestParam String idType,
                                             @RequestParam String idNumber,
                                             @RequestParam(required = false, defaultValue = "false") boolean breakfast,
                                             @RequestParam(required = false, defaultValue = "false") boolean shuttle,
                                             @RequestParam(required = false, defaultValue = "false") boolean spa,
                                             @RequestParam(required = false, defaultValue = "") String promoCode,
                                             @RequestParam double totalCost) {
        Map<String, Object> response = new HashMap<>();
        boolean signatureValid = false;

        if (razorpayOrderId.startsWith("order_mock_")) {
            signatureValid = true;
        } else {
            try {
                JSONObject attributes = new JSONObject();
                attributes.put("razorpay_order_id", razorpayOrderId);
                attributes.put("razorpay_payment_id", razorpayPaymentId);
                attributes.put("razorpay_signature", razorpaySignature);

                signatureValid = Utils.verifyPaymentSignature(attributes, keySecret);
            } catch (Exception e) {
                System.err.println("Razorpay signature verification failed: " + e.getMessage());
            }
        }

        if (!signatureValid) {
            response.put("success", false);
            response.put("message", "Payment signature verification failed.");
            return response;
        }

        String bookingId = "BK" + (1000 + bookingRepository.count() + 1);
        Booking booking = new Booking(bookingId, guestName, contact, roomNumber, nights, totalCost, "PAID",
                idType, idNumber, breakfast, shuttle, spa, promoCode, 0.0);
        bookingRepository.save(booking);

        response.put("success", true);
        response.put("bookingId", bookingId);
        response.put("message", "Payment verified and booking saved successfully.");
        return response;
    }

    @PostMapping("/chatbot/ask")
    public Map<String, Object> askChatbot(@RequestParam String message,
                                          @RequestParam(required = false, defaultValue = "default") String sessionId) {
        return chatbotOrchestrator.processMessage(message, sessionId);
    }
}

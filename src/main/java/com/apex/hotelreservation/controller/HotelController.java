package com.apex.hotelreservation.controller;

import com.apex.hotelreservation.model.Booking;
import com.apex.hotelreservation.model.Room;
import com.apex.hotelreservation.repository.BookingRepository;
import com.apex.hotelreservation.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
@CrossOrigin(origins = "*")
public class HotelController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @PostConstruct
    public void seedRooms() {
        if (roomRepository.count() == 0) {
            // Standard Rooms (101 - 105)
            for (int i = 101; i <= 105; i++) {
                roomRepository.save(new Room(i, "Standard", 100.00));
            }
            // Deluxe Rooms (201 - 205)
            for (int i = 201; i <= 205; i++) {
                roomRepository.save(new Room(i, "Deluxe", 180.00));
            }
            // Suite Rooms (301 - 303)
            for (int i = 301; i <= 303; i++) {
                roomRepository.save(new Room(i, "Suite", 350.00));
            }
        }
    }

    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @PostMapping("/book")
    public Map<String, Object> createBooking(@RequestParam String guestName,
                                             @RequestParam String contact,
                                             @RequestParam int roomNumber,
                                             @RequestParam int nights,
                                             @RequestParam String idType,
                                             @RequestParam String idNumber,
                                             @RequestParam(required = false, defaultValue = "false") boolean breakfast,
                                             @RequestParam(required = false, defaultValue = "false") boolean shuttle,
                                             @RequestParam(required = false, defaultValue = "false") boolean spa,
                                             @RequestParam(required = false, defaultValue = "") String promoCode) {
        Map<String, Object> response = new HashMap<>();

        // Validate room exists
        Room room = roomRepository.findById(roomNumber).orElse(null);
        if (room == null) {
            response.put("success", false);
            response.put("message", "Room not found.");
            return response;
        }

        // Validate room is not already booked
        List<Booking> activeBookings = bookingRepository.findAll();
        for (Booking b : activeBookings) {
            if (b.getRoomNumber() == roomNumber) {
                response.put("success", false);
                response.put("message", "Room is already booked.");
                return response;
            }
        }

        double roomCost = room.getPricePerNight() * nights;
        double breakfastCost = breakfast ? 15.0 * nights : 0.0;
        double shuttleCost = shuttle ? 30.0 : 0.0;
        double spaCost = spa ? 50.0 : 0.0;
        double subTotal = roomCost + breakfastCost + shuttleCost + spaCost;

        // Apply promo code discount if matches
        double discountPercent = 0.0;
        if (promoCode.equalsIgnoreCase("ROYAL20")) {
            discountPercent = 0.20;
        } else if (promoCode.equalsIgnoreCase("PALACE10")) {
            discountPercent = 0.10;
        }

        double discountAmount = subTotal * discountPercent;
        double totalCost = subTotal - discountAmount;

        String bookingId = "BK" + (1000 + bookingRepository.count() + 1);

        Booking booking = new Booking(bookingId, guestName, contact, roomNumber, nights, totalCost, "PAID",
                idType, idNumber, breakfast, shuttle, spa, promoCode, discountAmount);
        bookingRepository.save(booking);

        response.put("success", true);
        response.put("bookingId", bookingId);
        response.put("discountAmount", discountAmount);
        response.put("totalCost", totalCost);
        response.put("message", "Room booked successfully.");
        return response;
    }

    @DeleteMapping("/cancel/{id}")
    public Map<String, Object> cancelBooking(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        if (!bookingRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "Booking reference not found.");
            return response;
        }

        bookingRepository.deleteById(id);
        response.put("success", true);
        response.put("message", "Booking cancelled successfully. Refund initiated.");
        return response;
    }

    @GetMapping("/analytics")
    public Map<String, Object> getAnalytics() {
        Map<String, Object> response = new HashMap<>();
        List<Booking> bookings = bookingRepository.findAll();
        long totalRooms = roomRepository.count();
        long occupiedRooms = bookings.size();

        double occupancyRate = totalRooms > 0 ? ((double) occupiedRooms / totalRooms) * 100.0 : 0.0;
        double grossRevenue = bookings.stream().mapToDouble(Booking::getTotalCost).sum();
        double revPar = totalRooms > 0 ? grossRevenue / totalRooms : 0.0;

        Map<String, Integer> categoryDistribution = new HashMap<>();
        categoryDistribution.put("Standard", 0);
        categoryDistribution.put("Deluxe", 0);
        categoryDistribution.put("Suite", 0);

        for (Booking b : bookings) {
            Room r = roomRepository.findById(b.getRoomNumber()).orElse(null);
            if (r != null) {
                String cat = r.getCategory();
                categoryDistribution.put(cat, categoryDistribution.getOrDefault(cat, 0) + 1);
            }
        }

        response.put("totalRooms", totalRooms);
        response.put("occupiedRooms", occupiedRooms);
        response.put("occupancyRate", occupancyRate);
        response.put("grossRevenue", grossRevenue);
        response.put("revPar", revPar);
        response.put("categoryDistribution", categoryDistribution);

        return response;
    }
}

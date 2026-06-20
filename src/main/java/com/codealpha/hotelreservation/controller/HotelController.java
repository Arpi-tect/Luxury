package com.codealpha.hotelreservation.controller;

import com.codealpha.hotelreservation.model.Booking;
import com.codealpha.hotelreservation.model.Room;
import com.codealpha.hotelreservation.repository.BookingRepository;
import com.codealpha.hotelreservation.repository.RoomRepository;
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
                                             @RequestParam int nights) {
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

        double totalCost = room.getPricePerNight() * nights;
        String bookingId = "BK" + (1000 + bookingRepository.count() + 1);

        Booking booking = new Booking(bookingId, guestName, contact, roomNumber, nights, totalCost, "PAID");
        bookingRepository.save(booking);

        response.put("success", true);
        response.put("bookingId", bookingId);
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
}

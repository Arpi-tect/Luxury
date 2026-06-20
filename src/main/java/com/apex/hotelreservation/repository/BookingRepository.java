package com.apex.hotelreservation.repository;

import com.apex.hotelreservation.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {
}

package com.codealpha.hotelreservation.repository;

import com.codealpha.hotelreservation.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {
}

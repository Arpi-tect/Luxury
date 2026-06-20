package com.codealpha.hotelreservation.repository;

import com.codealpha.hotelreservation.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}

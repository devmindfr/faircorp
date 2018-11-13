package com.esme.spring.faircorp.repository;

import com.esme.spring.faircorp.model.Light;
import com.esme.spring.faircorp.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author devmind
 */
public interface RoomDao extends JpaRepository<Room, Long> {


}

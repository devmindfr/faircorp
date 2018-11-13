package com.esme.spring.faircorp.repository;

import com.esme.spring.faircorp.model.Building;
import com.esme.spring.faircorp.model.Light;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author devmind
 */
public interface BuildingDao extends JpaRepository<Building, Long> {


}

package com.esme.spring.faircorp.repository;

import com.esme.spring.faircorp.model.Light;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author devmind
 */
public interface LightDao extends JpaRepository<Light, Long>, LightDaoCustom {

    List<Light> findByRoomId(Long id);

    @Query("select l from Light l inner join l.room r inner join r.building b where b.id = :id")
    List<Light> findByBuildingId(Long id);

}

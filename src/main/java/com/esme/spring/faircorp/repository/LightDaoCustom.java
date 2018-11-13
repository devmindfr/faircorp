package com.esme.spring.faircorp.repository;

import com.esme.spring.faircorp.model.Light;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author devmind
 */
public interface LightDaoCustom {
    List<Light> findOnLights();
}

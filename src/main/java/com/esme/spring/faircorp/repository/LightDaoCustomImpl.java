package com.esme.spring.faircorp.repository;

import com.esme.spring.faircorp.model.Light;
import com.esme.spring.faircorp.model.Status;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author devmind
 */
@Repository
public class LightDaoCustomImpl implements LightDaoCustom{

    @PersistenceContext
    private EntityManager em;


    @Override
    public List<Light> findOnLights() {
        String jpql = "select lt from Light lt where lt.status = :value";
        return em.createQuery(jpql, Light.class)
                 .setParameter("value", Status.ON)
                 .getResultList();
    }
}

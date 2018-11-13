package com.esme.spring.faircorp.repository;

import com.esme.spring.faircorp.model.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


/**
 * @author devmind
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class LightDaoTest {
    @Autowired
    LightDao lightDao;

    @Test
    public void shouldFindOnLights() {
        assertThat(lightDao.findOnLights())
                .hasSize(1)
                .extracting("id", "status")
                .containsExactly(tuple(-1L, Status.ON));
    }


    @Test
    public void shouldFindByRoom() {
        assertThat(lightDao.findByRoomId(-10L))
                .hasSize(2)
                .extracting("id", "status")
                .containsExactly(tuple(-2L, Status.OFF), tuple(-1L, Status.ON));
    }

    @Test
    public void shouldFindByBuilding() {
        assertThat(lightDao.findByBuildingId(-10L))
                .hasSize(2)
                .extracting("id", "status")
                .containsExactly(tuple(-2L, Status.OFF), tuple(-1L, Status.ON));
    }


}
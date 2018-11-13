package com.esme.spring.faircorp.web;

import com.esme.spring.faircorp.model.Building;
import com.esme.spring.faircorp.model.Light;
import com.esme.spring.faircorp.model.Room;
import com.esme.spring.faircorp.model.Status;
import com.esme.spring.faircorp.repository.LightDao;
import com.esme.spring.faircorp.repository.RoomDao;
import com.esme.spring.faircorp.web.dto.LightDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author devmind
 */
@RunWith(SpringRunner.class)
@WebMvcTest(LightController.class)
public class LightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LightDao lightDao;

    @MockBean
    private RoomDao roomDao;

    @Test
    public void findAll() throws Exception {
        List<Light> expectedLights = Arrays.asList(new Light(createRoom(), 1, Status.ON),
                                                   new Light(createRoom(), 2, Status.OFF));

        BDDMockito.given(this.lightDao.findAll()).willReturn(expectedLights);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/lights"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                            List<Light> lights = deserializeLights(result);
                            Assertions.assertThat(lights)
                                      .hasSize(2)
                                      .extracting(Light::getLevel, Light::getStatus)
                                      .containsExactly(Tuple.tuple(1, Status.ON), Tuple.tuple(2, Status.OFF));
                        });
    }

    @Test
    public void findById() throws Exception {
        Light expectedLight = new Light(createRoom(), 1, Status.ON);
        BDDMockito.given(this.lightDao.findById(12L)).willReturn(Optional.of(expectedLight));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/lights/12"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                            Light light = deserializeLight(result);
                            Assertions.assertThat(light.getLevel()).isEqualTo(1);
                            Assertions.assertThat(light.getStatus()).isEqualTo(Status.ON);
                        });
    }

    @Test
    public void switchStatus() throws Exception {
        Light expectedLight = new Light(createRoom(), 1, Status.ON);
        BDDMockito.given(this.lightDao.findById(12L)).willReturn(Optional.of(expectedLight));

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/lights/12/switch"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                            Light light = deserializeLight(result);
                            Assertions.assertThat(light.getLevel()).isEqualTo(1);
                            Assertions.assertThat(light.getStatus()).isEqualTo(Status.OFF);
                        });
    }

    @Test
    public void findByUnknownId() throws Exception {
        BDDMockito.given(this.lightDao.findById(12L)).willReturn(Optional.empty());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/lights/12"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                            Light light = deserializeLight(result);
                            Assertions.assertThat(light).isNull();
                        });
    }

    @Test
    public void shouldCreateLight() throws Exception {
        BDDMockito.given(this.lightDao.findById(12L)).willReturn(Optional.empty());
        LightDto dto = createLightDto(12L, 2, Status.ON);

        Light expectedLight = new Light(createRoom(), 2, Status.ON);
        BDDMockito.given(this.lightDao.save(ArgumentMatchers.any(Light.class))).willReturn(expectedLight);

        // We just check the HTTP response code
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/lights")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk());

        // But we check also the call of the save method on the DAO
        BDDMockito.verify(this.lightDao, Mockito.atLeastOnce()).save(ArgumentMatchers.any(Light.class));
    }

    @Test
    public void shouldUpdateLight() throws Exception {
        LightDto dto = createLightDto(12L, 2, Status.OFF);

        Light expectedLight = new Light(createRoom(), 2, Status.OFF);
        BDDMockito.given(this.lightDao.findById(12L)).willReturn(Optional.of(expectedLight));

        // We just check the HTTP response code
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/lights")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(objectMapper.writeValueAsBytes(dto)))
                    .andExpect(MockMvcResultMatchers.status().isOk());

        // But we check also the call of the save method on the DAO
        BDDMockito.verify(this.lightDao, Mockito.atLeastOnce()).save(ArgumentMatchers.any(Light.class));
    }

    @Test
    public void shouldDeleteLight() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/lights/12"))
                    .andExpect(MockMvcResultMatchers.status().isOk());

        // We check the call of the delete method on the DAO
        BDDMockito.verify(this.lightDao, Mockito.atLeastOnce()).deleteById(ArgumentMatchers.eq(12L));
    }

    private List<Light> deserializeLights(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return Arrays.asList(objectMapper.readValue(content, Light[].class));
    }

    private Light deserializeLight(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return content.isEmpty() ? null : objectMapper.readValue(content, Light.class);
    }

    private Room createRoom() {
        Room room = new Room(new Building("Building"), "Room 1", 1);
        room.setId(12L);
        return room;
    }

    private LightDto createLightDto(Long lightId, int level, Status status) {
        LightDto dto = new LightDto();
        dto.setId(12L);
        dto.setLevel(level);
        dto.setStatus(status);
        dto.setRoomId(lightId);
        return dto;
    }
}
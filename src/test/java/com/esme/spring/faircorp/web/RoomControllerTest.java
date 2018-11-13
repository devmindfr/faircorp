package com.esme.spring.faircorp.web;

import com.esme.spring.faircorp.model.Building;
import com.esme.spring.faircorp.model.Room;
import com.esme.spring.faircorp.repository.BuildingDao;
import com.esme.spring.faircorp.repository.RoomDao;
import com.esme.spring.faircorp.repository.RoomDao;
import com.esme.spring.faircorp.web.dto.RoomDto;
import com.esme.spring.faircorp.web.dto.RoomDto;
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

import static org.junit.Assert.*;

/**
 * @author devmind
 */
@RunWith(SpringRunner.class)
@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc restService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomDao roomDao;
    
    @MockBean
    private BuildingDao buildingDao;

    @Test
    public void findAll() throws Exception {
        Room expectedRoom = new Room(createBuilding(), "Room 1", 1);
        List<Room> expectedRooms = Arrays.asList(expectedRoom);
        BDDMockito.given(this.roomDao.findAll()).willReturn(expectedRooms);

        this.restService.perform(MockMvcRequestBuilders.get("/api/rooms"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(result -> {
                            List<Room> rooms = deserializeRooms(result);
                            Assertions.assertThat(rooms)
                                      .hasSize(1)
                                      .extracting(Room::getName, Room::getFloor)
                                      .containsExactly(Tuple.tuple("Room 1", 1));
                        });
    }

    @Test
    public void findById() throws Exception {
        Room expectedRoom = new Room(createBuilding(), "Room 1", 1);
        BDDMockito.given(this.roomDao.findById(12L)).willReturn(Optional.of(expectedRoom));

        this.restService.perform(MockMvcRequestBuilders.get("/api/rooms/12"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(result -> {
                            Room room = deserializeRoom(result);
                            Assertions.assertThat(room.getName()).isEqualTo("Room 1");
                            Assertions.assertThat(room.getFloor()).isEqualTo(1);
                        });
    }

    @Test
    public void findByUnknownId() throws Exception {
        BDDMockito.given(this.roomDao.findById(12L)).willReturn(Optional.empty());

        this.restService.perform(MockMvcRequestBuilders.get("/api/rooms/12"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(result -> {
                            Room room = deserializeRoom(result);
                            Assertions.assertThat(room).isNull();
                        });
    }

    @Test
    public void shouldCreateRoom() throws Exception {
        BDDMockito.given(this.roomDao.findById(12L)).willReturn(Optional.empty());
        RoomDto dto = createRoomDto(12L, "Room 2", 2);

        Room expectedRoom = new Room(createBuilding(), "Room 2", 2);
        BDDMockito.given(this.roomDao.save(ArgumentMatchers.any(Room.class))).willReturn(expectedRoom);

        // We just check the HTTP response code
        this.restService.perform(MockMvcRequestBuilders.post("/api/rooms")
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsBytes(dto)))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isOk());

        // But we check also the call of the save method on the DAO
        BDDMockito.verify(this.roomDao, Mockito.atLeastOnce()).save(ArgumentMatchers.any(Room.class));
    }

    @Test
    public void shouldUpdateRoom() throws Exception {
        RoomDto dto = createRoomDto(12L, "Room 2", 2);

        Room expectedRoom = new Room(createBuilding(), "Room 2", 2);
        BDDMockito.given(this.roomDao.findById(12L)).willReturn(Optional.of(expectedRoom));

        // We just check the HTTP response code
        this.restService.perform(MockMvcRequestBuilders.post("/api/rooms")
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsBytes(dto)))
                        .andExpect(MockMvcResultMatchers.status().isOk());

        // But we check also the call of the save method on the DAO
        BDDMockito.verify(this.roomDao, Mockito.atLeastOnce()).save(ArgumentMatchers.any(Room.class));
    }

    @Test
    public void shouldDeleteRoom() throws Exception {

        this.restService.perform(MockMvcRequestBuilders.delete("/api/rooms/12"))
                        .andExpect(MockMvcResultMatchers.status().isOk());

        // We check the call of the delete method on the DAO
        BDDMockito.verify(this.roomDao, Mockito.atLeastOnce()).deleteById(ArgumentMatchers.eq(12L));
    }

    private List<Room> deserializeRooms(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return Arrays.asList(objectMapper.readValue(content, Room[].class));
    }

    private Room deserializeRoom(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return content.isEmpty() ? null : objectMapper.readValue(content, Room.class);
    }

    private Building createBuilding(){
        Building building = new Building("Building 1");
        building.setId(12L);
        return building;
    }

    private RoomDto createRoomDto(Long buildingId, String name, int floor){
        RoomDto dto = new RoomDto();
        dto.setId(12L);
        dto.setName(name);
        dto.setFloor(floor);
        dto.setBuildingId(buildingId);
        return dto;
    }
}
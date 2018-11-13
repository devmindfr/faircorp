package com.esme.spring.faircorp.web;

import com.esme.spring.faircorp.model.Building;
import com.esme.spring.faircorp.model.Status;
import com.esme.spring.faircorp.repository.BuildingDao;
import com.esme.spring.faircorp.web.dto.BuildingDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @author devmind
 */

@RunWith(SpringRunner.class)
@WebMvcTest(BuildingController.class)
public class BuildingControllerTest {

    @Autowired
    private MockMvc restService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BuildingDao buildingDao;


    @Test
    public void findAll() throws Exception {
        List<Building> expectedBuildings = Arrays.asList(new Building("Building 1"));
        BDDMockito.given(this.buildingDao.findAll()).willReturn(expectedBuildings);

        this.restService.perform(MockMvcRequestBuilders.get("/api/buildings"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(result -> {
                            List<Building> buildings = deserializeBuildings(result);
                            Assertions.assertThat(buildings)
                                      .hasSize(1)
                                      .extracting(Building::getName)
                                      .containsExactly("Building 1");
                        });
    }

    @Test
    public void findById() throws Exception {
        BDDMockito.given(this.buildingDao.findById(12L)).willReturn(Optional.of(new Building("Building 1")));

        this.restService.perform(MockMvcRequestBuilders.get("/api/buildings/12"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(result -> {
                            Building building = deserializeBuilding(result);
                            Assertions.assertThat(building.getName()).isEqualTo("Building 1");
                        });
    }

    @Test
    public void findByUnknownId() throws Exception {
        BDDMockito.given(this.buildingDao.findById(12L)).willReturn(Optional.empty());

        this.restService.perform(MockMvcRequestBuilders.get("/api/buildings/12"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(result -> {
                            Building building = deserializeBuilding(result);
                            Assertions.assertThat(building).isNull();
                        });
    }

    @Test
    public void shouldCreateBuilding() throws Exception {
        BDDMockito.given(this.buildingDao.findById(12L)).willReturn(Optional.empty());
        BuildingDto dto = createBuildingDtoWithName("Building 1");

        BDDMockito.given(this.buildingDao.save(ArgumentMatchers.any(Building.class))).willReturn(new Building("Building 1"));

        // We just check the HTTP response code
        this.restService.perform(MockMvcRequestBuilders.post("/api/buildings")
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsBytes(dto)))
                        .andDo(MockMvcResultHandlers.print())
                        .andExpect(MockMvcResultMatchers.status().isOk());

        // But we check also the call of the save method on the DAO
        BDDMockito.verify(this.buildingDao, Mockito.atLeastOnce()).save(ArgumentMatchers.any(Building.class));
    }

    @Test
    public void shouldUpdateBuilding() throws Exception {
        BuildingDto dto = createBuildingDtoWithName("Building 2");

        BDDMockito.given(this.buildingDao.findById(12L)).willReturn(Optional.of(new Building("Building 1")));

        // We just check the HTTP response code
        this.restService.perform(MockMvcRequestBuilders.post("/api/buildings")
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsBytes(dto)))
                        .andExpect(MockMvcResultMatchers.status().isOk());

        // But we check also the call of the save method on the DAO
        BDDMockito.verify(this.buildingDao, Mockito.atLeastOnce()).save(ArgumentMatchers.any(Building.class));
    }

    @Test
    public void shouldDeleteBuilding() throws Exception {

        this.restService.perform(MockMvcRequestBuilders.delete("/api/buildings/12"))
                        .andExpect(MockMvcResultMatchers.status().isOk());

        // We check the call of the delete method on the DAO
        BDDMockito.verify(this.buildingDao, Mockito.atLeastOnce()).deleteById(ArgumentMatchers.eq(12L));
    }

    private List<Building> deserializeBuildings(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return Arrays.asList(objectMapper.readValue(content, Building[].class));
    }

    private Building deserializeBuilding(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return content.isEmpty() ? null : objectMapper.readValue(content, Building.class);
    }

    private BuildingDto createBuildingDtoWithName(String name){
        BuildingDto dto = new BuildingDto();
        dto.setId(12L);
        dto.setName(name);
        return dto;
    }
}
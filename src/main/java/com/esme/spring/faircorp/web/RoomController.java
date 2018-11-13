package com.esme.spring.faircorp.web;

import com.esme.spring.faircorp.model.Room;
import com.esme.spring.faircorp.repository.BuildingDao;
import com.esme.spring.faircorp.repository.RoomDao;
import com.esme.spring.faircorp.web.dto.RoomDto;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author devmind
 */
@CrossOrigin
@RestController
@RequestMapping("/api/rooms")
@Transactional
public class RoomController {

    private RoomDao roomDao;
    private BuildingDao buildingDao;

    public RoomController(RoomDao roomDao, BuildingDao buildingDao) {
        this.roomDao = roomDao;
        this.buildingDao = buildingDao;
    }

    @GetMapping
    public List<RoomDto> findAll() {
        return roomDao.findAll()
                      .stream()
                      .sorted(Comparator.comparing(Room::getName))
                      .map(RoomDto::new)
                      .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public RoomDto findById(@PathVariable Long id) {
        return roomDao.findById(id).map(room -> new RoomDto(room)).orElse(null);
    }

    @PostMapping
    public RoomDto create(@RequestBody RoomDto dto) {
        Room room = null;
        if (dto.getId() != null) {
            room = roomDao.findById(dto.getId()).orElse(null);
        }

        if (room == null) {
            room = roomDao.save(new Room(buildingDao.getOne(dto.getBuildingId()),
                                         dto.getName(),
                                         dto.getFloor()));
        } else {
            room.setFloor(dto.getFloor());
            room.setName(dto.getName());
            roomDao.save(room);
        }

        return new RoomDto(room);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        roomDao.deleteById(id);
    }
}

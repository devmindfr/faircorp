package com.esme.spring.faircorp.web;

import com.esme.spring.faircorp.model.Light;
import com.esme.spring.faircorp.model.Status;
import com.esme.spring.faircorp.repository.LightDao;
import com.esme.spring.faircorp.repository.RoomDao;
import com.esme.spring.faircorp.web.dto.LightDto;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author devmind
 */
@RestController
@RequestMapping("/api/hello")
@Transactional
public class HelloController {


    @GetMapping("/{name}")
    public MessageDto welcome(@PathVariable String name) {
        return new MessageDto("Hello " + name);
    }


    class MessageDto {
        String message;

        public MessageDto(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

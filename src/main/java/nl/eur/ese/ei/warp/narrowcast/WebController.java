package nl.eur.ese.ei.warp.narrowcast;

import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@org.springframework.stereotype.Controller
public class WebController {

    private final RoomService roomService;

    @Autowired
    public WebController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/empty")
    public String getEmptyRooms(Model model) {
        model.addAttribute("rooms", roomService.getEmptyRoomsToday());
        return "emptyrooms";
    }

    @GetMapping("/occupants")
    public String getOccupants(Model model) {
        model.addAttribute("entries", roomService.getUsersPerRoom());
        return "occupants";
    }

    @GetMapping("/locations")
    public String getLocations(Model model) {
        model.addAttribute("entries", roomService.getRoomsPerUser());
        return "locations";
    }

}

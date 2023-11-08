package nl.eur.ese.ei.warp.narrowcast;

import nl.eur.ese.ei.warp.narrowcast.RoomService;
import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Profile("api")
@Controller
public class ApiController {
    private final RoomService roomService;

    @Autowired
    public ApiController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/api/today")
    public ResponseEntity<Set<Book>> getToday() {
        return ResponseEntity.ok(roomService.getActiveBookingsToday());
    }

    @GetMapping("/api/current")
    public ResponseEntity<Set<Book>> getCurrent() {
        return ResponseEntity.ok(roomService.getActiveBookings());
    }

    @GetMapping("/api/empty")
    public ResponseEntity<Set<String>> getEmptyRooms() {
        return ResponseEntity.ok(roomService.getEmptyRoomsToday());
    }

}

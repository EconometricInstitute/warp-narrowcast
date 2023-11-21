package nl.eur.ese.ei.warp.narrowcast;

import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import nl.eur.ese.ei.warp.narrowcast.entities.Seat;
import nl.eur.ese.ei.warp.narrowcast.repos.BookRepository;
import nl.eur.ese.ei.warp.narrowcast.repos.SeatRepository;
import nl.eur.ese.ei.warp.narrowcast.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("!mock")
public class DatabaseRoomService extends RoomServiceAdapter implements RoomService {


    private final BookRepository bookRepo;
    private final SeatRepository seatRepo;

    @Autowired
    public DatabaseRoomService(BookRepository bookRepo, SeatRepository seatRepo, ConfigProperties config) {
        super(config);
        this.bookRepo = bookRepo;
        this.seatRepo = seatRepo;
    }

    @Override
    public Set<Book> getActiveBookings(long timestamp) {
        return bookRepo.findBooksForTimestamp(timestamp);
    }

    @Override
    public List<Book> getActiveBookings(long timestamp, String room) {
        return bookRepo.findBooksForTimestampAndRoom(timestamp, room);
    }


    @Override
    public Set<String> getEmptyRoomsBetween(Pair<OffsetDateTime> interval) {
        Set<Book> bookings = getActiveBookings(interval);
        Set<Seat> seats = seatRepo.getSeats();
        TreeSet<String> rooms = new TreeSet<>();
        seats.forEach(s -> rooms.add(s.getRoom()));
        bookings.forEach(b -> rooms.remove(b.getSeat().getRoom()));
        return rooms;
    }

    @Override
    public List<String> getRooms() {
        return seatRepo.getSeats()
                .stream()
                .map(Seat::getRoom)
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Set<Book> getActiveBookings(Pair<OffsetDateTime> interval) {
        return new TreeSet<>(
                bookRepo.findBooksInRange(
                        config.getDatabaseTimestamp(interval.first()),
                        config.getDatabaseTimestamp(interval.second())
                )
        );
    }

}

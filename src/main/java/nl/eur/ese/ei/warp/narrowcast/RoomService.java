package nl.eur.ese.ei.warp.narrowcast;

import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import nl.eur.ese.ei.warp.narrowcast.entities.Seat;
import nl.eur.ese.ei.warp.narrowcast.repos.BookRepository;
import nl.eur.ese.ei.warp.narrowcast.repos.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final BookRepository bookRepo;
    private final SeatRepository seatRepo;

    @Autowired
    public RoomService(BookRepository bookRepo, SeatRepository seatRepo) {
        this.bookRepo = bookRepo;
        this.seatRepo = seatRepo;
    }

    public Set<Book> getActiveBookings() {
        return getActiveBookings(OffsetDateTime.now().toEpochSecond());
    }

    public Map<String, List<String>> getUsersPerRoom() {
        return getUsersPerRoom(OffsetDateTime.now().toEpochSecond());
    }

    public Map<String, List<String>> getUsersPerRoom(long timestamp) {
        Map<String, List<Book>> active =
            getActiveBookings(timestamp)
            .stream()
            .collect(Collectors.groupingBy(book -> book.getSeat().getRoom()));
        Map<String, List<String>> result = new TreeMap<>();
        for (var entry : active.entrySet()) {
            List<String> users = entry
                    .getValue()
                    .stream()
                    .map(book -> book.getUser().getName())
                    .distinct()
                    .sorted()
                    .toList();
            result.put(entry.getKey(), users);
        }
        return result;
    }

    public Map<String, List<String>> getRoomsPerUser() {
        return getRoomsPerUser(OffsetDateTime.now().toEpochSecond());
    }

    public Map<String, List<String>> getRoomsPerUser(long timestamp) {
        Map<String, List<Book>> active =
                getActiveBookings(timestamp)
                        .stream()
                        .collect(Collectors.groupingBy(book -> book.getUser().getName()));
        Map<String, List<String>> result = new TreeMap<>();
        for (var entry : active.entrySet()) {
            List<String> users = entry
                    .getValue()
                    .stream()
                    .map(book -> book.getSeat().getRoom())
                    .distinct()
                    .sorted()
                    .toList();
            result.put(entry.getKey(), users);
        }
        return result;
    }

    public Set<Book> getActiveBookings(long timestamp) {
        return bookRepo.findBooksForTimestamp(timestamp);
    }

    public Set<String> getEmptyRoomsToday() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        OffsetDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        return getEmptyRoomsBetween(startOfDay.toEpochSecond(), endOfDay.toEpochSecond());
    }

    public Set<String> getEmptyRoomsBetween(long start, long end) {
        Set<Book> bookings = bookRepo.findBooksInRange(start, end);
        Set<Seat> seats = seatRepo.getSeats();
        TreeSet<String> rooms = new TreeSet<>();
        seats.forEach(s -> rooms.add(s.getRoom()));
        bookings.forEach(b -> rooms.remove(b.getSeat().getRoom()));
        return rooms;
    }
}

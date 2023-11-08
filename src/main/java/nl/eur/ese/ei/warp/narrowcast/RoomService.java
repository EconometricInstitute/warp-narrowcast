package nl.eur.ese.ei.warp.narrowcast;

import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import nl.eur.ese.ei.warp.narrowcast.entities.Seat;
import nl.eur.ese.ei.warp.narrowcast.repos.BookRepository;
import nl.eur.ese.ei.warp.narrowcast.repos.SeatRepository;
import nl.eur.ese.ei.warp.narrowcast.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomService {


    private final BookRepository bookRepo;
    private final SeatRepository seatRepo;
    private final ConfigProperties config;

    @Autowired
    public RoomService(BookRepository bookRepo, SeatRepository seatRepo, ConfigProperties config) {
        this.bookRepo = bookRepo;
        this.seatRepo = seatRepo;
        this.config = config;
    }

    public Map<String, List<String>> getUsersPerRoom() {
        return getUsersPerRoom(config.getDatabaseNowTimestamp());
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
                    .collect(Collectors.toList());
            result.put(entry.getKey(), users);
        }
        return result;
    }

    public List<String> getUsersForRoom(String room) {
        return getUsersForRoom(config.getDatabaseNowTimestamp(), room);
    }

    public List<String> getUsersForRoom(long timestamp, String room) {
        List<Book> active = getActiveBookings(timestamp, room);
        return active
                .stream()
                .map(book -> book.getUser().getName())
                .collect(Collectors.toList());

    }

    public Map<String, List<String>> getRoomsPerUser() {
        return getRoomsPerUser(config.getDatabaseNowTimestamp());
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
                    .collect(Collectors.toList());
            result.put(entry.getKey(), users);
        }
        return result;
    }

    public Set<Book> getActiveBookings() {
        return getActiveBookings(config.getDatabaseNowTimestamp());
    }

    public Set<Book> getActiveBookings(long timestamp) {
        return bookRepo.findBooksForTimestamp(timestamp);
    }

    public List<Book> getActiveBookings(long timestamp, String room) {
        return bookRepo.findBooksForTimestampAndRoom(timestamp, room);
    }


    public Pair<OffsetDateTime> getTodayPair() {
        return getDayPair(OffsetDateTime.now());
    }

    public Pair<OffsetDateTime> getDayPair(OffsetDateTime instant) {
        LocalDateTime start = LocalDateTime.of(instant.getYear(), instant.getMonth(), instant.getDayOfMonth(), 0, 0);
        LocalDateTime end = LocalDateTime.of(instant.getYear(), instant.getMonth(), instant.getDayOfMonth(), 23, 59);
        OffsetDateTime startOfDay = start.atZone(config.getDatabaseTimezone()).toOffsetDateTime();
        OffsetDateTime endOfDay = end.atZone(config.getDatabaseTimezone()).toOffsetDateTime();
        return Pair.of(startOfDay, endOfDay);
    }

    public Set<String> getEmptyRoomsToday() {
        return getEmptyRoomsBetween(getTodayPair());
    }

    public Set<String> getEmptyRoomsBetween(Pair<OffsetDateTime> interval) {
        Set<Book> bookings = getActiveBookings(interval);
        Set<Seat> seats = seatRepo.getSeats();
        TreeSet<String> rooms = new TreeSet<>();
        seats.forEach(s -> rooms.add(s.getRoom()));
        bookings.forEach(b -> rooms.remove(b.getSeat().getRoom()));
        return rooms;
    }

    public List<String> getRooms() {
        return seatRepo.getSeats()
                .stream()
                .map(Seat::getRoom)
                .sorted()
                .collect(Collectors.toList());
    }

    public Set<Book> getActiveBookings(Pair<OffsetDateTime> interval) {
        return new TreeSet<>(
                bookRepo.findBooksInRange(
                        config.getDatabaseTimestamp(interval.first()),
                        config.getDatabaseTimestamp(interval.second())
                )
        );
    }

    public Set<Book> getActiveBookingsToday() {
        return getActiveBookings(getTodayPair());
    }

}

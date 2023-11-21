package nl.eur.ese.ei.warp.narrowcast;

import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import nl.eur.ese.ei.warp.narrowcast.util.Pair;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoomService {
    Map<String, List<String>> getUsersPerRoom();

    Map<String, List<String>> getUsersPerRoom(long timestamp);

    List<String> getUsersForRoom(String room);

    List<String> getUsersForRoom(long timestamp, String room);

    Map<String, List<String>> getRoomsPerUser();

    Map<String, List<String>> getRoomsPerUser(long timestamp);

    Set<Book> getActiveBookings();

    Set<Book> getActiveBookings(long timestamp);

    List<Book> getActiveBookings(long timestamp, String room);

    Pair<OffsetDateTime> getTodayPair();

    Pair<OffsetDateTime> getDayPair(OffsetDateTime instant);

    Set<String> getEmptyRoomsToday();

    Set<String> getEmptyRoomsBetween(Pair<OffsetDateTime> interval);

    List<String> getRooms();

    Set<Book> getActiveBookings(Pair<OffsetDateTime> interval);

    Set<Book> getActiveBookingsToday();
}

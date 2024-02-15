/*
 * warp_narrowcast - application to display important information related to seat reservations
 * Copyright (C) 2023-2024 Paul Bouman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.eur.ese.ei.warp.narrowcast.service;

import nl.eur.ese.ei.warp.narrowcast.ConfigProperties;
import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import nl.eur.ese.ei.warp.narrowcast.entities.Seat;
import nl.eur.ese.ei.warp.narrowcast.entities.User;
import nl.eur.ese.ei.warp.narrowcast.util.Pair;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Profile("mock")
public class MockRoomService extends RoomServiceAdapter {

    private static final long AROUND_MAX_MINUTES = 60*4;

    // List of some random names taken from the internet
    private static final List<User> names = List.of(new User("1", "Fatima Haynes"),
            new User("2", "Jordon Underwood"),
            new User("3", "Camila Buck"),
            new User("4", "Douglas Livingston"),
            new User("5", "Nathanael Russell"),
            new User("6", "Caiden Haynes"),
            new User("7", "Rayna Pennington"),
            new User("8", "Payton Alexander"),
            new User("9", "Cohen Hines"),
            new User("10", "Nola Diaz"),
            new User("11", "Aubrey Wilkerson"),
            new User("12", "Ally Krueger"),
            new User("13", "Jaydan Roy"),
            new User("14", "Cheyenne Golden"),
            new User("15", "Jazlynn Schmitt"),
            new User("16", "Zachery Ayers"),
            new User("17", "Ronin Phelps"),
            new User("18", "Gage Barton"),
            new User("19", "Mario Powell"),
            new User("20", "Francesca Obrien"),
            new User("21", "Rayan Bolton"),
            new User("22", "Kaila Hebert"),
            new User("23", "Trevon Yoder"),
            new User("24", "Khalil Lyons"),
            new User("25", "Antonio Ford"),
            new User("26", "Zachariah Acevedo"),
            new User("27", "Jovany Rivers"),
            new User("28", "Brynlee Campos"),
            new User("29", "Justus Hancock"),
            new User("30", "Maria Buck"),
            new User("31", "Deon Yu"),
            new User("32", "Ashlynn Gonzalez"),
            new User("33", "Keira May"),
            new User("34", "Alyssa York"),
            new User("35", "Aiden Fisher"),
            new User("36", "Asa Acevedo"),
            new User("37", "Owen Doyle"),
            new User("38", "Cassius Hebert"),
            new User("39", "Ronnie Randall"),
            new User("40", "Aurora Cruz"));

    // List of random seat names that sound as if they could be from our office
    private static final List<Seat> seats = List.of(
            new Seat(1L, "ET01-a", true),
            new Seat(2L, "ET01-b", true),
            new Seat(3L, "ET02-a", true),
            new Seat(4L, "ET03-a", true),
            new Seat(5L, "ET04-a", true),
            new Seat(6L, "ET04-b", true),
            new Seat(7L, "ET05-a", true),
            new Seat(8L, "ET05-b", true),
            new Seat(9L, "ET06-a", true),
            new Seat(10L, "ET06-c", true),
            new Seat(11L, "ET06-c", true),
            new Seat(12L, "ET06-d", true),
            new Seat(13L, "ET07-a", true),
            new Seat(14L, "ET07-b", true),
            new Seat(15L, "ET07-c", true),
            new Seat(16L, "ET07-d", true),
            new Seat(17L, "ET08-a", true),
            new Seat(18L, "ET09-a", true),
            new Seat(19L, "ET10-a", true),
            new Seat(20L, "ET11-a", true),
            new Seat(21L, "ET11-b", true),
            new Seat(22L, "ET12-a", true),
            new Seat(23L, "ET12-b", true),
            new Seat(24L, "ET13-a", true),
            new Seat(25L, "ET14-a", true),
            new Seat(26L, "ET15-a", true),
            new Seat(27L, "ET15-b", true),
            new Seat(28L, "ET15-c", true),
            new Seat(29L, "ET16-a", true),
            new Seat(30L, "ET17-a", true),
            new Seat(31L, "ET18-a", true),
            new Seat(32L, "ET18-b", true),
            new Seat(33L, "ET19-a", true),
            new Seat(34L, "ET19-b", true),
            new Seat(35L, "ET19-c", true),
            new Seat(36L, "ET19-d", true));

    public MockRoomService(ConfigProperties config) {
        super(config);
    }

    @Override
    public Set<Book> getActiveBookings(long timestamp) {
        Random random = ThreadLocalRandom.current();
        List<Seat> shuffle = new ArrayList<>(seats);
        Collections.shuffle(shuffle, random);
        OffsetDateTime rel = OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), config.getApplicationTimezone());
        return generateRandomBookings(random, shuffle, () -> generateAround(random, rel));
    }

    @Override
    public List<Book> getActiveBookings(long timestamp, String room) {
        Random random = ThreadLocalRandom.current();
        List<Seat> shuffle = seats.
                stream()
                .filter(s -> s.getRoom().equals(room))
                .filter(s -> random.nextBoolean())
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(shuffle, random);
        OffsetDateTime rel = OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), config.getApplicationTimezone());
        return new ArrayList<>(generateRandomBookings(random, shuffle, () -> generateAround(random, rel)));
    }

    @Override
    public Set<String> getEmptyRoomsBetween(Pair<OffsetDateTime> interval) {
        Random random = ThreadLocalRandom.current();
        return getRooms()
                .stream()
                .filter(ignored -> random.nextBoolean())
                .collect(Collectors.toSet());
    }

    @Override
    public List<String> getRooms() {
        return  seats.stream()
                .map(Seat::getRoom)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Set<Book> getActiveBookings(Pair<OffsetDateTime> interval) {
        OffsetDateTime from = interval.first();
        OffsetDateTime to = interval.second();
        Duration delta = Duration.between(from, to);
        long deltaMinutes = delta.toMinutes();
        if (deltaMinutes <= 0) {
            return Collections.emptySet();
        }

        Random random = ThreadLocalRandom.current();

        List<Seat> shuffle = new ArrayList<>(seats);
        Collections.shuffle(shuffle);

        return generateRandomBookings(random, shuffle, () -> generateOverlapping(random, from, deltaMinutes));
    }

    private Pair<OffsetDateTime> generateAround(Random random, OffsetDateTime time) {
        return Pair.of(
                time.minusMinutes(1 + random.nextLong(AROUND_MAX_MINUTES)),
                time.plusMinutes(1 + random.nextLong(AROUND_MAX_MINUTES))
        );
    }

    private Pair<OffsetDateTime> generateOverlapping(Random random, OffsetDateTime from, long deltaMinutes) {
        OffsetDateTime bookFrom, bookUntil;
        OffsetDateTime bookRef = from.plusMinutes(random.nextLong(deltaMinutes));
        if (random.nextBoolean()) {
            bookFrom = bookRef.minusMinutes(1 + random.nextLong(deltaMinutes));
            bookUntil = bookRef;
        }
        else {
            bookFrom = bookRef;
            bookUntil = bookRef.plusMinutes(1 + random.nextLong(deltaMinutes));
        }
        return Pair.of(bookFrom, bookUntil);
    }

    private Set<Book> generateRandomBookings(Random random, List<Seat> shuffle, Supplier<Pair<OffsetDateTime>> intervalGenerator) {
        Set<Book> bookings = new LinkedHashSet<>();

        long id = 10_000 + random.nextLong(10_000);
        for (User user : names) {
            if (shuffle.isEmpty()) {
                // No more seats left
                break;
            }
            // Use a probability here?
            if (random.nextBoolean()) {
                Pair<OffsetDateTime> pair = intervalGenerator.get();
                OffsetDateTime bookFrom = pair.first();
                OffsetDateTime bookUntil = pair.second();
                Seat seat = shuffle.remove(0);
                Book book = new Book(id++, user, seat,
                        (int) config.getDatabaseTimestamp(bookFrom),
                        (int) config.getDatabaseTimestamp(bookUntil)
                );
                bookings.add(book);
            }
        }

        return bookings;
    }
}

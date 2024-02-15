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

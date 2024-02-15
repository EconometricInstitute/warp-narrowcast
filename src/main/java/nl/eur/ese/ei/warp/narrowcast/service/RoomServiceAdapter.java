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
import nl.eur.ese.ei.warp.narrowcast.service.RoomService;
import nl.eur.ese.ei.warp.narrowcast.util.Pair;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class RoomServiceAdapter implements RoomService {
    protected final ConfigProperties config;

    public RoomServiceAdapter(ConfigProperties config) {
        this.config = config;
    }

    public Map<String, List<String>> getUsersPerRoom() {
        return getUsersPerRoom(config.getDatabaseNowTimestamp());
    }

    public Map<String, List<String>> getRoomsPerUser() {
        return getRoomsPerUser(config.getDatabaseNowTimestamp());
    }

    public Set<Book> getActiveBookings() {
        return getActiveBookings(config.getDatabaseNowTimestamp());
    }

    public Pair<OffsetDateTime> getTodayPair() {
        return getDayPair(OffsetDateTime.now());
    }

    public Set<String> getEmptyRoomsToday() {
        return getEmptyRoomsBetween(getTodayPair());
    }

    public Pair<OffsetDateTime> getDayPair(OffsetDateTime instant) {
        LocalDateTime start = LocalDateTime.of(instant.getYear(), instant.getMonth(), instant.getDayOfMonth(), 0, 0);
        LocalDateTime end = LocalDateTime.of(instant.getYear(), instant.getMonth(), instant.getDayOfMonth(), 23, 59);
        OffsetDateTime startOfDay = start.atZone(config.getDatabaseTimezone()).toOffsetDateTime();
        OffsetDateTime endOfDay = end.atZone(config.getDatabaseTimezone()).toOffsetDateTime();
        return Pair.of(startOfDay, endOfDay);
    }

    public Set<Book> getActiveBookingsToday() {
        return getActiveBookings(getTodayPair());
    }

    @Override
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

    @Override
    public List<String> getUsersForRoom(String room) {
        return getUsersForRoom(config.getDatabaseNowTimestamp(), room);
    }

    @Override
    public List<String> getUsersForRoom(long timestamp, String room) {
        List<Book> active = getActiveBookings(timestamp, room);
        return active
                .stream()
                .map(book -> book.getUser().getName())
                .collect(Collectors.toList());

    }

    @Override
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
}

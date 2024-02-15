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

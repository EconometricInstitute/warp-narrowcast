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

package nl.eur.ese.ei.warp.narrowcast.controllers;

import nl.eur.ese.ei.warp.narrowcast.service.RoomService;
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

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

import nl.eur.ese.ei.warp.narrowcast.ConfigProperties;
import nl.eur.ese.ei.warp.narrowcast.service.RoomService;
import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    private final RoomService roomService;
    private final ConfigProperties config;

    @Autowired
    public WebController(RoomService roomService, ConfigProperties config) {
        this.roomService = roomService;
        this.config = config;
    }

    @GetMapping({"", "/"})
    public String getIndex(Model model) {
        model.addAttribute("rooms", roomService.getRooms());
        return "index";
    }

    @GetMapping("/empty")
    public String getEmptyRooms(Model model) {
        model.addAttribute("rooms", roomService.getEmptyRoomsToday());
        return "emptyrooms";
    }

    @GetMapping("/occupants")
    public String getOccupants(Model model) {
        model.addAttribute("entries", roomService.getUsersPerRoom());
        return "occupants";
    }

    @GetMapping("/locations")
    public String getLocations(Model model) {
        model.addAttribute("entries", roomService.getRoomsPerUser());
        return "locations";
    }

    @GetMapping("/today")
    public String getTable(Model model,
                           @RequestParam(defaultValue="false") boolean timestamps,
                           @RequestParam(required = false) Book.BookOrder order) {
        List<Book> bookings = new ArrayList<>(roomService.getActiveBookingsToday());
        if (order != null) {
            bookings.sort(order);
        }
        model.addAttribute("bookings", bookings);
        model.addAttribute("config", config);
        model.addAttribute("timestamps", timestamps);
        return "bookings";
    }

    @GetMapping("/rooms/{room}")
    public String getRoom(Model model, @PathVariable String room) {

        List<String> persons = roomService.getUsersForRoom(room);
        model.addAttribute("room", room);
        model.addAttribute("persons", persons);
        return "room";
    }
}

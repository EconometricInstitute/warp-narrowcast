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

package nl.eur.ese.ei.warp.narrowcast.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name="seat")
public class Seat {

    private static final List<String> ROOM_SEPARATORS = List.of("-", "_");
    private static final String UNKNOWN_ROOM = "UNKNOWN";

    @Id
    Long id;

    @Column(name="name")
    private String name;
    @Column(name="enabled")
    private boolean enabled;

    protected Seat() {}

    public Seat(Long id, String name, boolean enabled) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        for (String separator : ROOM_SEPARATORS) {
            if (name.contains(separator)) {
                return name.substring(0, name.lastIndexOf(separator));
            }
        }
        return UNKNOWN_ROOM;
    }

    public boolean isEnabled() {
        return enabled;
    }
}

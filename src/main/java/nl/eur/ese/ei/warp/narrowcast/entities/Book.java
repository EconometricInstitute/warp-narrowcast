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

import jakarta.persistence.*;
import nl.eur.ese.ei.warp.narrowcast.ConfigProperties;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Comparator;

@Entity
public class Book implements Comparable<Book> {

    private static final Comparator<Book> NATURAL_ORDER =
            Comparator.comparing(Book::getFromTimestamp)
                    .thenComparing(Book::getUntilTimestamp)
                    .thenComparing(book -> book.getUser().getName())
                    .thenComparing(book -> book.getSeat().getName())
                    .thenComparing(Book::getId);

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name="login", referencedColumnName="login")
    private User user;

    @ManyToOne
    @JoinColumn(name="sid", referencedColumnName = "id")
    private Seat seat;

    @Column(name="fromts")
    private int fromTimestamp;
    @Column(name="tots")
    private int untilTimestamp;

    protected Book() {}

    public Book(Long id, User user, Seat seat, int fromTimestamp, int untilTimestamp) {
        this.id = id;
        this.user = user;
        this.seat = seat;
        this.fromTimestamp = fromTimestamp;
        this.untilTimestamp = untilTimestamp;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Seat getSeat() {
        return seat;
    }

    public int getFromTimestamp() {
        return fromTimestamp;
    }

    public OffsetDateTime getFrom(ConfigProperties config) {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(fromTimestamp), config.getDatabaseTimezone());
    }

    public int getUntilTimestamp() {
        return untilTimestamp;
    }

    public OffsetDateTime getUntil(ConfigProperties config) {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(untilTimestamp), config.getDatabaseTimezone());
    }

    @Override
    public int compareTo(Book other) {
        return NATURAL_ORDER.compare(this, other);
    }

    public enum BookOrder implements Comparator<Book> {

        NAME(Comparator.comparing(book -> book.getUser().getName())),
        ROOM(Comparator.comparing(book -> book.getSeat().getRoom())),
        FROM(Comparator.naturalOrder()),
        UNTIL(Comparator.comparing(Book::getUntilTimestamp));

        private final Comparator<Book> order;
        BookOrder(Comparator<Book> comparator) {
            this.order = comparator.thenComparing(Comparator.naturalOrder());
        }


        @Override
        public int compare(Book o1, Book o2) {
            return order.compare(o1, o2);
        }
    }
}

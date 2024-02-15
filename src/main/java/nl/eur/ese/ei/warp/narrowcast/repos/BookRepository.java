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

package nl.eur.ese.ei.warp.narrowcast.repos;

import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BookRepository extends Repository<Book, Long> {

    @Query("FROM Book b WHERE b.fromTimestamp <= :timestamp AND b.untilTimestamp >= :timestamp AND b.seat.enabled")
    Set<Book> findBooksForTimestamp(@Param("timestamp") long timestamp);

    @Query("From Book b WHERE b.fromTimestamp <= :timestamp AND b.untilTimestamp >= :timestamp AND b.seat.enabled AND b.seat.name LIKE :room% ORDER BY b ASC")
    List<Book> findBooksForTimestampAndRoom(@Param("timestamp") long timestamp, @Param("room") String room);

    @Query("FROM Book b WHERE b.fromTimestamp < :until AND b.untilTimestamp > :from AND b.seat.enabled")
    Set<Book> findBooksInRange(@Param("from") long from, @Param("until") long until);

}

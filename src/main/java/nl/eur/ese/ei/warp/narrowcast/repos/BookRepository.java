package nl.eur.ese.ei.warp.narrowcast.repos;

import nl.eur.ese.ei.warp.narrowcast.entities.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface BookRepository extends Repository<Book, Long> {

    @Query("FROM Book b WHERE b.fromTimestamp <= :timestamp AND b.untilTimestamp >= :timestamp AND b.seat.enabled")
    Set<Book> findBooksForTimestamp(@Param("timestamp") long timestamp);

    @Query("FROM Book b WHERE b.fromTimestamp < :until AND b.untilTimestamp > :from AND b.seat.enabled")
    Set<Book> findBooksInRange(@Param("from") long from, @Param("until") long until);

}

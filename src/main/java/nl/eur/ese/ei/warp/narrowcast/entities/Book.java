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

package nl.eur.ese.ei.warp.narrowcast.entities;

import jakarta.persistence.*;

@Entity
public class Book {

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

    public int getUntilTimestamp() {
        return untilTimestamp;
    }
}

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

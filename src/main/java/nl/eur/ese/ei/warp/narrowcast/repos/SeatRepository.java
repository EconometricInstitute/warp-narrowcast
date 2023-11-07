package nl.eur.ese.ei.warp.narrowcast.repos;

import nl.eur.ese.ei.warp.narrowcast.entities.Seat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Set;

public interface SeatRepository extends Repository<Seat,Long> {

    @Query("FROM Seat s")
    Set<Seat> getSeats();

}

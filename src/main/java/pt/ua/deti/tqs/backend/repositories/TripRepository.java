package pt.ua.deti.tqs.backend.repositories;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.constants.TripStatus;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {
    List<Trip> findByDepartureAndStatusNotInOrderByDepartureTimeAsc(City city, List<TripStatus> statuses, Limit limit);
    List<Trip> findByArrivalAndStatusNotOrderByArrivalTimeAsc(City city,  TripStatus status, Limit limit);
}

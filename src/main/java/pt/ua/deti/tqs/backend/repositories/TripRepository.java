package pt.ua.deti.tqs.backend.repositories;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.entities.City;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long>, JpaSpecificationExecutor<Trip> {
    List<Trip> findByDepartureOrderByDepartureTimeAsc(City city, Limit limit);
    List<Trip> findByArrivalOrderByArrivalTimeAsc(City city, Limit limit);
}

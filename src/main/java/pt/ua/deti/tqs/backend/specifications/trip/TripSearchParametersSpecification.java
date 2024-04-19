package pt.ua.deti.tqs.backend.specifications.trip;

import io.micrometer.common.lang.NonNullApi;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pt.ua.deti.tqs.backend.entities.Trip;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NonNullApi
public class TripSearchParametersSpecification implements Specification<Trip> {
    private final TripSearchParameters params;

    @Override
    public Predicate toPredicate(Root<Trip> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        if (params.getDeparture() != null) {
            predicates.add(builder.equal(root.get("departure").get("id"), params.getDeparture()));
        }

        if (params.getArrival() != null) {
            predicates.add(builder.equal(root.get("arrival").get("id"), params.getArrival()));
        }

        if (params.getDepartureTime() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("departureTime"), params.getDepartureTime()));
        }

        if (params.getFreeSeats() != null) {
            predicates.add(builder.ge(root.get("freeSeats"), params.getFreeSeats()));
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

package pt.ua.deti.tqs.backend.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import pt.ua.deti.tqs.backend.constants.TripStatus;
import pt.ua.deti.tqs.backend.entities.Trip;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripStatusSchedulerService {

    private final TripService tripService;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void updateStatus() {
        try {
            log.info("Updating status of trips");
            List<Trip> trips = tripService.getAllTrips();

            for (Trip trip : trips) {
                updateTripStatus(trip);
            }
        } catch (Exception e) {
            log.error("Error occurred while updating trip statuses: {}", e.getMessage(), e);
        }
    }

    public void updateTripStatus(Trip trip) {
        if (trip.getStatus() == TripStatus.ONTIME && 
            ChronoUnit.MINUTES.between(LocalDateTime.now(), trip.getDepartureTime()) <= 9) { // means 10 minutes before departure, it rounds up the difference
            trip.setStatus(TripStatus.ONBOARDING);
            tripService.updateTrip(trip);
        }
    }
}

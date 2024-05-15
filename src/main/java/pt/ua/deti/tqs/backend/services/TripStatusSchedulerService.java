package pt.ua.deti.tqs.backend.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import pt.ua.deti.tqs.backend.constants.TripStatus;
import pt.ua.deti.tqs.backend.entities.City;
import pt.ua.deti.tqs.backend.entities.Trip;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripStatusSchedulerService {

    private SimpMessagingTemplate template;

    private final TripService tripService;

    private final CityService cityService;  

    @Scheduled(fixedDelayString = "${trip.status.update.delay}")
    @Transactional
    public void updateStatus() {
        try {
            log.info("Updating status of trips..");
            List<Trip> trips = tripService.getAllTrips();

            for (Trip trip : trips) {
                updateTripStatus(trip);
            }

            log.info("Sending updates to digital signages..");
            List<City> allCities = cityService.getAllCities();

            for (City city : allCities) {
                List<Trip> departureTrips = tripService.getTripsForDigitalSignageDeparture(city);
                List<Trip> arrivalTrips = tripService.getTripsForDigitalSignageArrival(city);
                template.convertAndSend("/signage/cities" + city.getName() + "/departure", departureTrips);
                template.convertAndSend("/signage/cities" + city.getName() + "/departure", arrivalTrips);
            }
        } catch (Exception e) {
            log.error("Error occurred while updating trip statuses: {}", e.getMessage(), e);
        }
    }

    public void updateTripStatus(Trip trip) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = trip.getDepartureTime();
        TripStatus status = trip.getStatus();
        int delay = trip.getDelay();
    
        if ((status == TripStatus.ONTIME || status == TripStatus.DELAYED) &&
            ChronoUnit.MINUTES.between(now, departureTime.plusMinutes(delay)) <= 9) { // means 10 minutes before departure, it rounds up the difference
            trip.setStatus(TripStatus.ONBOARDING);
            tripService.updateTrip(trip);
        } 
        else if (status == TripStatus.ONBOARDING && 
                (ChronoUnit.MINUTES.between(now, departureTime) <= 0 ||
                (delay > 0 && ChronoUnit.MINUTES.between(now, departureTime.plusMinutes(delay)) <= 0))) {
            trip.setStatus(TripStatus.DEPARTED);
            tripService.updateTrip(trip);
        }
    }
}

package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.entities.Reservation;
import pt.ua.deti.tqs.backend.entities.Trip;
import pt.ua.deti.tqs.backend.entities.User;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.repositories.ReservationRepository;
import pt.ua.deti.tqs.backend.repositories.TripRepository;

import java.util.List;
import java.util.Collections;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;
    private final TripService tripService;
    private final UserService userService;
    private final CurrencyService currencyService;

    public Reservation createReservation(Reservation reservation, Currency currency) {
        Trip trip = tripService.getTrip(reservation.getTrip().getId(), currency);
        List<Reservation> tripReservations = reservationRepository.findByTripId(trip.getId());

        if (trip.getFreeSeats() < reservation.getSeats().size()) {
            return null;
        }
        
        for (Reservation savedReservation : tripReservations) {
            if (!Collections.disjoint(savedReservation.getSeats(), reservation.getSeats())) {
                return null;
            }
        }

        User user = userService.getUser(reservation.getUser().getId());
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setPrice(trip.getPrice() * reservation.getSeats().size());

        Reservation save = reservationRepository.save(reservation);
        trip.calculateFreeSeats();
        tripRepository.save(trip);
        return save;
    }

    public List<Reservation> getAllReservations(Currency currency) {
        List<Reservation> all = reservationRepository.findAll();

        updateReservationPrices(all, currency);
        return all;
    }

    public Reservation getReservation(Long id, Currency currency) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);

        updateReservationPrices(reservation, currency);
        return reservation;
    }

    public List<Reservation> getReservationsByUserId(Long userId, Currency currency) {
        List<Reservation> all = reservationRepository.findByUserId(userId);

        updateReservationPrices(all, currency);
        return all;
    }

    public List<Reservation> getReservationsByTripId(Long tripId, Currency currency) {
        List<Reservation> all = reservationRepository.findByTripId(tripId);

        updateReservationPrices(all, currency);
        return all;
    }

    public void deleteReservationById(Long id) {
        Trip trip = reservationRepository.findById(id).map(Reservation::getTrip).orElse(null);
        reservationRepository.deleteById(id);

        if (trip != null) {
            trip.calculateFreeSeats();
            tripRepository.save(trip);
        }
    }

    private void updateReservationPrices(List<Reservation> all, Currency currency) {
        all.forEach(reservation -> updateReservationPrices(reservation, currency));
    }

    private void updateReservationPrices(Reservation reservation, Currency currency) {
        if (reservation != null && currency != null && currency != Currency.EUR) {
            reservation.setPrice(
                    currencyService.convertEurToCurrency(reservation.getPrice(), currency));
            reservation.getTrip().setPrice(
                    currencyService.convertEurToCurrency(reservation.getTrip().getPrice(), currency));
        }
    }
}

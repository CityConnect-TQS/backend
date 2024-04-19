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
import java.util.Optional;

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

        if (trip.getFreeSeats() < reservation.getSeats()) {
            return null;
        }

        User user = userService.getUser(reservation.getUser().getId());
        reservation.setTrip(trip);
        reservation.setUser(user);
        reservation.setPrice(trip.getPrice() * reservation.getSeats());

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

    public Reservation updateReservation(Reservation reservation, Currency currency) {
        Optional<Reservation> existingOpt = reservationRepository.findById(reservation.getId());
        Trip trip = tripService.getTrip(reservation.getTrip().getId(), currency);

        if (existingOpt.isEmpty()) {
            return null;
        }

        Reservation existing = existingOpt.get();
        existing.setUser(reservation.getUser());
        existing.setSeats(reservation.getSeats());
        existing.setTrip(reservation.getTrip());
        reservation.setPrice(trip.getPrice() * reservation.getSeats());

        Reservation save = reservationRepository.save(reservation);
        reservation.getTrip().calculateFreeSeats();
        tripRepository.save(reservation.getTrip());
        return save;
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

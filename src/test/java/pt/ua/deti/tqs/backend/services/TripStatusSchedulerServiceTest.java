package pt.ua.deti.tqs.backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.ua.deti.tqs.backend.constants.TripStatus;
import pt.ua.deti.tqs.backend.entities.Trip;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripStatusSchedulerServiceTest {
    
    @Mock(lenient = true)
    private TripService tripService;

    @InjectMocks
    private TripStatusSchedulerService tripSchedulerService;

    @Test
    void givenOnTimeBus_whenOnBoardingTimeArrives_thenStatusChanges(){

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setDepartureTime(LocalDateTime.now().plusMinutes(10));

        tripSchedulerService.updateTripStatus(trip);

        assertThat(trip.getStatus()).isEqualTo(TripStatus.ONBOARDING);
        verify(tripService, Mockito.times(1)).updateTrip(trip);
    }

    @Test
    void givenOnTimeBus_whenOnBoardingTimeArrives_thenStatusChanges2(){

        Trip trip = new Trip();
        trip.setId(2L);
        trip.setDepartureTime(LocalDateTime.now());

        tripSchedulerService.updateTripStatus(trip);

        assertThat(trip.getStatus()).isEqualTo(TripStatus.ONBOARDING);
        verify(tripService, Mockito.times(1)).updateTrip(trip);
    }

    @Test
    void givenOnTimeBus_whenNotOnBoardingTime_thenStatusDoesntChange(){

        Trip trip = new Trip();
        trip.setId(3L);
        trip.setDepartureTime(LocalDateTime.now().plusHours(1));

        tripSchedulerService.updateTripStatus(trip);

        assertThat(trip.getStatus()).isEqualTo(TripStatus.ONTIME);
        verify(tripService, Mockito.times(0)).updateTrip(trip);
    }

    @Test
    void givenOnTimeBus_whenNotOnBoardingTime_thenStatusDoesntChange2(){

        Trip trip = new Trip();
        trip.setId(4L);
        trip.setDepartureTime(LocalDateTime.now().plusMinutes(11)); 

        tripSchedulerService.updateTripStatus(trip);

        assertThat(trip.getStatus()).isEqualTo(TripStatus.ONTIME);
        verify(tripService, Mockito.times(0)).updateTrip(trip);
    }

    @Test
    void givenOnTimeBus_whenNotOnBoardingTime_thenStatusDoesntChange3(){

        Trip trip = new Trip();
        trip.setId(5L);
        trip.setDepartureTime(LocalDateTime.now()); 
        trip.setStatus(TripStatus.DELAYED);

        tripSchedulerService.updateTripStatus(trip);

        assertThat(trip.getStatus()).isEqualTo(TripStatus.DELAYED);
        verify(tripService, Mockito.times(0)).updateTrip(trip);
    }
}

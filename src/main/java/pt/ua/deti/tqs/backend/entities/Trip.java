package pt.ua.deti.tqs.backend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pt.ua.deti.tqs.backend.constants.TripStatus;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private City departure;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    @ManyToOne
    @JoinColumn(nullable = false)
    private City arrival;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    @Column
    private double price;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Bus bus;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Setter(AccessLevel.NONE)
    private Collection<Reservation> reservations;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "freeSeats")
    private Integer freeSeats;

    public void calculateFreeSeats() {
        this.freeSeats = bus.getCapacity() - (reservations != null ? reservations.stream()
                                                                                 .reduce(0,
                                                                                         (acc, r) -> acc + r.getSeats(),
                                                                                         Integer::sum) : 0);
    }

    @Column(nullable = false)
    private TripStatus status = TripStatus.ONTIME;

    @Column
    private Integer delay = 0;
}

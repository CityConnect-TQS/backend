package pt.ua.deti.tqs.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Collection<String> seats;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double price;

    @Column
    @JoinColumn(nullable = false)
    private boolean checkedIn = false;

}

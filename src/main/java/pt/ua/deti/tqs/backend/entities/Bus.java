package pt.ua.deti.tqs.backend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String company;

    @OneToMany(mappedBy = "bus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Setter(AccessLevel.NONE)
    private Collection<Trip> trip;
}

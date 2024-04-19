package pt.ua.deti.tqs.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ua.deti.tqs.backend.entities.Bus;

public interface BusRepository extends JpaRepository<Bus, Long> {
}

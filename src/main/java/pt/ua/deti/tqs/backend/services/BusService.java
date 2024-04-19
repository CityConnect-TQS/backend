package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.entities.Bus;
import pt.ua.deti.tqs.backend.repositories.BusRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BusService {
    private BusRepository busRepository;

    public Bus createBus(Bus bus) {
        return busRepository.save(bus);
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus getBus(Long id) {
        return busRepository.findById(id).orElse(null);
    }

    public Bus updateBus(Bus bus) {
        Optional<Bus> existingOpt = busRepository.findById(bus.getId());

        if (existingOpt.isEmpty()) {
            return null;
        }

        Bus existing = existingOpt.get();
        existing.setCapacity(bus.getCapacity());
        return busRepository.save(existing);
    }

    public void deleteBus(Long id) {
        busRepository.deleteById(id);
    }
}

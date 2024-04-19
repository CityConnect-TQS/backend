package pt.ua.deti.tqs.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Stats {
    private int totalRequests;
    private int cacheMisses;
}

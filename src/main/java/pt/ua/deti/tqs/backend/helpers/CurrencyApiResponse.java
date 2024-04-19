package pt.ua.deti.tqs.backend.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyApiResponse {
    private Map<Currency, Double> data;
}
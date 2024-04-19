package pt.ua.deti.tqs.backend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.backend.helpers.Currency;

import java.util.Map;

@Service
@AllArgsConstructor
public class CurrencyService {
    private final ApiService apiService;
    private final StatsService statsService;

    public double convertEurToCurrency(double euros, Currency currency) {
        if (currency == Currency.EUR) {
            return euros;
        }

        statsService.incrementTotalRequests();
        Map<Currency, Double> response = apiService.fetchRates();
        return euros * response.get(currency);
    }

    public double convertCurrencyToEur(double amount, Currency currency) {
        if (currency == Currency.EUR) {
            return amount;
        }

        statsService.incrementTotalRequests();
        Map<Currency, Double> response = apiService.fetchRates();
        return amount / response.get(currency);
    }
}

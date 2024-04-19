package pt.ua.deti.tqs.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.ua.deti.tqs.backend.helpers.Currency;
import pt.ua.deti.tqs.backend.helpers.CurrencyApiResponse;

import java.util.Map;

@Service
@Slf4j
public class ApiService {
    private final String apiKey;
    private final RestTemplate restTemplate;
    private final StatsService statsService;

    public ApiService(@Value("${currencyapi.apikey:null}") String apiKey, RestTemplate restTemplate, StatsService statsService) {
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
        this.statsService = statsService;
    }

    @Cacheable("rates")
    public Map<Currency, Double> fetchRates() {
        log.info("Fetching rates");
        statsService.incrementCacheMisses();
        CurrencyApiResponse forObject = restTemplate.getForEntity(
                "https://api.freecurrencyapi.com/v1/latest?apikey=" + apiKey + "&base_currency=" + Currency.EUR,
                CurrencyApiResponse.class).getBody();
        return forObject != null ? forObject.getData() : null;
    }

    @CacheEvict(value = "rates", allEntries = true)
    @Scheduled(fixedRate = 3600000)
    public void emptyRatesCache() {
        log.info("Emptying rates cache");
    }
}

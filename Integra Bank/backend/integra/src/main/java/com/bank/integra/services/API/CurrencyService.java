package com.bank.integra.services.API;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrencyService {
    @Autowired
    private RestTemplate restTemplate;
    private final String apiUrl = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=11";

    public CurrencyService () {}

    public Map<String, Map<String, String>> getUsdExchangeRate() {
        List<Map<String, String>> exchangeRates = Arrays.asList(restTemplate.getForObject(apiUrl, Map[].class));

        return exchangeRates.stream()
                .filter(rate -> "USD".equals(rate.get("ccy")))
                .collect(Collectors.toMap(
                        rate -> rate.get("ccy"),
                        rate -> Map.of("buy", rate.get("buy"), "sale", rate.get("sale"))
                ));
    }
}

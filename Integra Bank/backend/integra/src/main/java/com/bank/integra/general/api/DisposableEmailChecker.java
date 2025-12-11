package com.bank.integra.general.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@Slf4j
public class DisposableEmailChecker {
    private final RestTemplate restTemplate;
    private final String API_URL;

    public DisposableEmailChecker(RestTemplate restTemplate, @Value("${app.api-disposable-email-url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.API_URL = apiUrl;
    }



    public boolean isEmailDisposable(String email) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("email", email)
                .toUriString();

        try {
            Map<String, String> response = restTemplate.getForObject(url, Map.class);
            return Boolean.parseBoolean(response.get("disposable"));
        } catch(Exception e) {
            String errorMessage = "Ошибка запроса: " + e.getMessage();
            log.error(errorMessage);
            return true;
        }
    }

}

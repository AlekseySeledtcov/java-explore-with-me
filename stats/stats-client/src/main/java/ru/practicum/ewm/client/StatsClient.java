package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(RestTemplateBuilder builder, @Value("${ewm-main-service.url}") String serverUrl) {
        this.serverUrl = serverUrl;
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public ResponseEntity<EndpointHitDto> postHit(EndpointHitDto dto) {
        return restTemplate.postForEntity(
                serverUrl + "/hit",
                dto,
                EndpointHitDto.class
        );
    }

    public ResponseEntity<ViewStatsDto> getStats(LocalDateTime start,
                                                 LocalDateTime end,
                                                 List<String> uris,
                                                 Boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(DateTimeFormatter.ISO_DATE_TIME))
                .queryParam("end", end.format(DateTimeFormatter.ISO_DATE_TIME));

        if (uris != null) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        if (unique != null) {
            builder.queryParam("unique", unique);
        }

        String url = builder.toUriString();

        return restTemplate.getForEntity(url, ViewStatsDto.class);
    }
}

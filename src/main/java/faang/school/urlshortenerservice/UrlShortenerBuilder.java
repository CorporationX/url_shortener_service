package faang.school.urlshortenerservice;

import lombok.*;


@RequiredArgsConstructor
public class UrlShortenerBuilder {

    private final String baseUrl = "https://localhost:8080/api/v1/";

    private final String hash;

    public String fullHashedUrl() {
        return baseUrl + hash;
    }
}

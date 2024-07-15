package faang.school.urlshortenerservice.service.search;

import java.util.Optional;

public interface SearchesService {
    Optional<String> findUrl(String hash);
}

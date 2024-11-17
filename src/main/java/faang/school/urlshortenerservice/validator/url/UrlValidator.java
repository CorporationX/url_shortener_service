package faang.school.urlshortenerservice.validator.url;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlValidator {

    private final UrlRepository paymentRepository;

    public void validateUrlDoesNotExist(String url) {
        if (paymentRepository.existsByUrl(url)) {
            throw new DataValidationException("URL " + url + " already exists");
        }
    }
}



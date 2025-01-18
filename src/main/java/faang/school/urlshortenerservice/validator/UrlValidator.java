package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlValidator {

    private final UrlRepository urlRepository;

    public void validateUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new DataValidationException("Malformed URL " + url + " " + e);
        }
    }

    public String urlExistsInBase(String longUrl) {
        Optional<Url> url = urlRepository.findByUrl(longUrl);
        return url.map(Url::getHash).orElse(null);
    }

}

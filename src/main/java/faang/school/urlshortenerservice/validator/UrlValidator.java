package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class UrlValidator {

    private static final String URL_REGEX =
            "^(https?|ftp)://[\\w.-]+(?:\\.[\\w.-]+)+[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]+";

    private final UrlRepository urlRepository;

    public boolean isValidUrl(String urlString) {
        Pattern pattern = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlString);
        return matcher.matches();
    }

    public String urlExistsInBase(String longUrl) {
        Optional<Url> url = urlRepository.findByUrl(longUrl);
        return url.map(Url::getHash).orElse(null);
    }

}

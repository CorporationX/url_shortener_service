package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;

public class UrlValidator {

    private final String urlRegexp = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$";

    public void validateUrl(String verifiableUrl) {
        if (verifiableUrl == null ||
                verifiableUrl.isEmpty() ||
                !urlRegexp.matches(verifiableUrl)) {
            throw new DataValidationException("This url not validation");
        }
    }

    public void validateSearchUrl(UrlEntity searchUrlEntity, String hash) {
        if (searchUrlEntity == null || searchUrlEntity.getUrl() == null) {
            throw new UrlNotFoundException("URL not found for hash: " + hash);
        }
    }
}

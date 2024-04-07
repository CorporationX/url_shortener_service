package faang.school.urlshortenerservice.validator;

import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;

public class UrlValidator {

    public void validateSearchUrl(UrlEntity searchUrlEntity, String hash) {
        if (searchUrlEntity == null || searchUrlEntity.getUrl() == null) {
            throw new UrlNotFoundException("URL not found for hash: " + hash);
        }
    }
}

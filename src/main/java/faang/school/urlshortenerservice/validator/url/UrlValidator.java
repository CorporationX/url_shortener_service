package faang.school.urlshortenerservice.validator.url;

import faang.school.urlshortenerservice.entity.url.Url;

public interface UrlValidator {

    Url findByUrl(String url);
}

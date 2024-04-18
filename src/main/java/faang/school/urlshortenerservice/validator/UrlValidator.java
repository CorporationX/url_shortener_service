package faang.school.urlshortenerservice.validator;

public interface UrlValidator {
    void validateHash(String hash);

    void validateUrl(String url);
}

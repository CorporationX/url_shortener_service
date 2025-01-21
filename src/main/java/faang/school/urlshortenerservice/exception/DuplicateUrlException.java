package faang.school.urlshortenerservice.exception;

public class DuplicateUrlException extends RuntimeException{
    private static final String RESOURCE_NOT_FOUND = "URL already exists in the database.";

    public DuplicateUrlException(String url) {
        super(String.format(RESOURCE_NOT_FOUND, url));
    }
}

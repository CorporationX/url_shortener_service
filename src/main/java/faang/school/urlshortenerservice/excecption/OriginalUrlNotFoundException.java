package faang.school.urlshortenerservice.excecption;

public class OriginalUrlNotFoundException extends RuntimeException {
    public OriginalUrlNotFoundException(String message) {
        super(message);
    }
}

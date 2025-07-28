package faang.school.urlshortenerservice.exceptions;

public class IllegalHashLength extends RuntimeException {
    public IllegalHashLength(String message) {
        super(message);
    }
}

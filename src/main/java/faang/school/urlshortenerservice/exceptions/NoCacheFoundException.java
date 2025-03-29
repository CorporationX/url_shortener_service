package faang.school.urlshortenerservice.exceptions;


public class NoCacheFoundException  extends RuntimeException {
    public NoCacheFoundException(String message) {
        super(message);
    }
}

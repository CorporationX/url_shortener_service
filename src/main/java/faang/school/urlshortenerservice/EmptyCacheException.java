package faang.school.urlshortenerservice;

public class EmptyCacheException extends RuntimeException {
    public EmptyCacheException(String msg) {
        super(msg);
    }

    EmptyCacheException (String msg, Throwable cause) {
        super(msg, cause);
    }
}

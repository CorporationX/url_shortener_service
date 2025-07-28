package faang.school.urlshortenerservice.exception.authorization;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String msg) {
        super(msg);
    }
}

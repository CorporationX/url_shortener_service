package faang.school.urlshortenerservice.exception;

public class EmptyCacheException extends BusinessException {
    public EmptyCacheException(String code, String msg) {
        super(code, msg);
    }
}

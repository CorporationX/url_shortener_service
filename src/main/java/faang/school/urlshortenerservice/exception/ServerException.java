package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public abstract class ServerException extends RuntimeException {

    private final String code;
    private final String message;

    public ServerException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

package faang.school.urlshortenerservice.exception;

public class Base62EncoderException extends RuntimeException {

    public Base62EncoderException(String message) {
        super(message);
    }
    public Base62EncoderException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }

}

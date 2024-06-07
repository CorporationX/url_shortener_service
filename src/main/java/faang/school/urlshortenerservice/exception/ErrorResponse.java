package faang.school.urlshortenerservice.exception;

import java.time.LocalDateTime;

public record ErrorResponse (String message, LocalDateTime time){

}

package faang.school.urlshortenerservice.controller;

import java.time.LocalDateTime;

public record ErrorResponse (String message, LocalDateTime time){

};

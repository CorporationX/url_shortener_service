package faang.school.urlshortenerservice.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ErrorDto {
    private final UUID id;
    private final String message;

    public ErrorDto(String message) {
        this.id = UUID.randomUUID();
        this.message = message;
    }
}

package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestUlrDto {

    @NotBlank
    private String url;
}

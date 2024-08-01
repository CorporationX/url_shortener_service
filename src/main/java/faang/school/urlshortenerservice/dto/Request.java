package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @NotNull(message = "URL cannot be null")
    @NotBlank(message = "URL cannot be blank")
    @URL(message = "Invalid URL")
    private String url;
}
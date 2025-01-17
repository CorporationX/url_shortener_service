package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDto {

    @NotNull
    @URL(message = "Invalid url")
    private String url;

}

package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUrlDto {
    @NotNull(message = "Url should not be null")
    @URL(message = "URL format incorrect")
    private String url;
}

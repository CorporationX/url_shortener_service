package faang.school.urlshortenerservice.dto;


import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlCreateDto {
    @Pattern(regexp = "^(https?)://[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+([/?].*)?$",
            message = "Неверный формат URL")
    private String originalUrl;
}

package faang.school.urlshortenerservice.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    @NotBlank(message = "Url cannot be empty")
    @URL(
            regexp = ".*\\.(com|org|net|ru)(:[0-9]{1,5})?(/.*)?$",
            message = "URL must be valid and end with .com, .org, .net or .ru"
    )
    private String url;
}

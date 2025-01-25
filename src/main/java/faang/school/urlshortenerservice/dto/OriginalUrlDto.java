package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
public class OriginalUrlDto {

    @URL(message = "Invalid URL format")
    @NotBlank(message = "URl can not be blank")
    private String url;
}

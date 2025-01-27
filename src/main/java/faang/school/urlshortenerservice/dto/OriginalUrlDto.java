package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginalUrlDto {

    @URL(message = "Invalid URL format")
    @NotBlank(message = "URl can not be blank")
    private String url;
}

package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @URL(message = "The URL must be a valid URL format, including the protocol (e.g., http:// or https://)")
    @NotNull(message = "Original url must be specified")
    @Length(max = 512, message = "Original URL length must be less than 512 characters")
    private String url;
}

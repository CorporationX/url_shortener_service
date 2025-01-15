package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @NotNull(message = "Original url must be specified")
    @Length(min = 3, max = 512)
    private String url;
}

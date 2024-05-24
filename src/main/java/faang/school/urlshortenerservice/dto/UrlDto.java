package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {
    @URL(message = "Invalid URL format")
    @NotNull(message = "URL can not be null")
    private String longUrl;

    @Size(max = 6, message = "Hash size can not be longer than 6 symbols")
    private String hash;


}

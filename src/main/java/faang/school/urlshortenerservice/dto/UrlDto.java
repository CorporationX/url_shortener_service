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

    @Size(max = 6, message = "The size of the hash cannot be bigger then 6 symbols")
    private String hash;

    @NotNull(message = "URL cannot be null")
    @URL(message = "Invalid URL format")
    private String baseUrl;

}
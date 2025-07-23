package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlDto {
    @NotNull(message = "URL must be not Null")
    @NotBlank(message = "Url can not be Empty")
    private String shortUrl;

    @NotNull(message = "Hash must be not null")
    @NotBlank(message = "Hash can not be Empty")
    private String hash;
    }
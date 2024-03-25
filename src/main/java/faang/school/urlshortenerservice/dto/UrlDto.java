package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @Size(max = 6, message = "Hash should not exceed 6 characters")
    private String hash;
    @NotNull(message = "URL cant be null")
    @NotBlank(message = "URL cant be blank")
    private String url;
    private LocalDateTime createdAt;
}

package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UrlDto {
    private String hash;
    @NotNull
    @URL(message = "Incorrect Url")
    private String url;
    private LocalDateTime createdAt;
}

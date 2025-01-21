package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("url")
@Getter
public class UrlDto {

    @NotNull(message = "Url cannot be null")
    @NotBlank(message = "Url cannot be empty")
    private String url;
}

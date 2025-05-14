package faang.school.urlshortenerservice.dto.hash;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HashRequestDto {
    @NotEmpty
    @Max(6)
    private String hash;
}

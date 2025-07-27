package faang.school.urlshortenerservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class FullUrlRequestDto {
    @NotNull
    @Length(min = 6, max = 6)
    private String hash;
}

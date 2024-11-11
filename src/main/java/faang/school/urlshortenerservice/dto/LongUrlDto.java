package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LongUrlDto {
    @NotNull(message = "Url cannot be null")
    @NotEmpty(message = "Url cannot be empty")
    private String url;
}

package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UrlToShortDto {
    @NotNull(message = "Url cannot be null")
    @NotEmpty(message = "Url cannot be empty")
    @Size(max = 128, message = "Url cannot exceed 128 characters")
    private String url;
}

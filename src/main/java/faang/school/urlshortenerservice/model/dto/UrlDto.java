package faang.school.urlshortenerservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
public class UrlDto {
    @Null(groups = Create.class, message = "Id must be null")
    private Long id;

    @NotNull(groups = Create.class, message = "Original url can not be null")
    @NotBlank(groups = Create.class, message = "Original url can not be empty or blank")
    @URL(groups = Create.class)
    private String originalUrl;

    @Null(groups = Create.class, message = "Short url must be null")
    private String shortUrl;

    @Null(groups = Create.class, message = "CreatedAt must be null")
    private LocalDateTime createdAt;

    @Null(groups = Create.class, message = "UpdatedAt must be null")
    private LocalDateTime updatedAt;

    public interface Create {

    }
}

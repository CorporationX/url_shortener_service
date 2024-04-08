package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class RequestUrl {
    @NotBlank(message = "Url cannot be blank")
    @URL(message = "Url mast be valid")
    private String url;
}

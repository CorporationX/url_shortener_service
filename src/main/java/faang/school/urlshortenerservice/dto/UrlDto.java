package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto implements Serializable {
    @NotBlank(message = "Url should not be blank")
    @URL(message = "Url should be correct")
    private String url;
}

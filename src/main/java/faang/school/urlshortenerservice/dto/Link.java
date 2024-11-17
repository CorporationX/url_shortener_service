package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Link {

    @NotBlank(message = "Url cannot be blank")
    @URL(message = "Url must be valid")
    @Size(max = 1024, message = "Content must be no more than 1024 characters")
    private String link;
}

package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * DTO (Data Transfer Object) для передачи длинного URL.
 * Используется для создания короткого URL.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LongUrlDto {

    /**
     * Длинный URL, который нужно сократить.
     * Должен быть валидным URL (проверяется аннотацией @URL).
     */
    @URL
    private String url;
}

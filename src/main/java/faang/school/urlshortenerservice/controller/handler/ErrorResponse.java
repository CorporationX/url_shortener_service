package faang.school.urlshortenerservice.controller.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Класс, представляющий стандартизированный ответ об ошибке.
 * Используется для возврата информации об ошибках в API.
 */
@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    /**
     * URL запроса, в котором произошла ошибка.
     */
    private String url;

    /**
     * HTTP-статус ошибки.
     */
    private int status;

    /**
     * Тип ошибки (например, "Validation Error", "Entity Not Found").
     */
    private String error;

    /**
     * Сообщение об ошибке.
     */
    private String message;

    /**
     * Время возникновения ошибки.
     */
    private LocalDateTime timestamp;
}

package faang.school.url_shortener_service.config.swagger.annotations;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Schema(
        description = "The original URL that needs to be shortened",
        example = "https://example.com/long-url"
)
public @interface ApiUrlRequestDto {
}

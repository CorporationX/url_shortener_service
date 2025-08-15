package faang.school.urlshortenerservice.document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.Date;

@Data
@Builder
@Document(collection = "short_url")
public class ShortUrl {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @Field(value = "code", targetType = FieldType.STRING)
    private String code;

    @Field(value = "original_url", targetType = FieldType.STRING)
    private String originalUrl;

    @CreatedDate
    @Indexed(expireAfterSeconds = 157680000)
    @Field(value = "created_at", targetType = FieldType.DATE_TIME)
    private Date createdAt;

}

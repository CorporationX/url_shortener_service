package faang.school.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("urls")
public class UrlHash {

    @PrimaryKey
    @Column("hash")
    private String hash;

    @Column("full_url")
    private String fullUrl;

    @CreatedDate
    @Column("created_at")
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private LocalDateTime createdAt;
}

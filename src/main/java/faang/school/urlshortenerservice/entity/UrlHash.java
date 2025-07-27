package faang.school.urlshortenerservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("urls")
public class UrlHash {

    @PrimaryKey
    @Column("hash")
    private String hash;

    @Column("full_url")
    private String fullUrl;
}

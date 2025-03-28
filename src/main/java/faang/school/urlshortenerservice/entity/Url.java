package faang.school.urlshortenerservice.entity;

import faang.school.urlshortenerservice.entity.base.CreateAudit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "urls")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Url extends CreateAudit {
    @Id
    @Column(name = "hash", length = 6)
    private String hash;

    @Column(name = "url", length = 2048, nullable = false)
    private String url;
}

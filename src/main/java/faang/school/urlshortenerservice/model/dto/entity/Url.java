package faang.school.urlshortenerservice.model.dto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "url")
public class Url {

    @Id
    @OneToOne()
    @JoinColumn(name = "hash", referencedColumnName = "hash")
    private Hash hash;

    @Column(name = "url", updatable = false, nullable = false)
    private String url;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime created_at;
}

package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Url implements Serializable {

    @Id
    @Column(name = "hash", nullable = false)
    public String hash;

    @Column(name = "url")
    public String url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

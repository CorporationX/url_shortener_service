package faang.school.urlshortenerservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Url")
public class Hash implements Serializable {

    @Id
    @Size(max = 6)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String hash;
}

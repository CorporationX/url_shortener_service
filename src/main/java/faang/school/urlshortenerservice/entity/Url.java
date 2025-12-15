package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Data
@NoArgsConstructor
public class Url {

	@Id
	@Column(name = "hash", nullable = false, unique = true, length = 6)
	private String hash;

	@Column(name = "url", nullable = false)
	private String url;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public Url(String hash, String url) {
		this.hash = hash;
		this.url = url;
		this.createdAt = LocalDateTime.now();
	}
}

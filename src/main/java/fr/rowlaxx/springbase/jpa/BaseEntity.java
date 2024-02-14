package fr.rowlaxx.springbase.jpa;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID uuid;

	@Column(updatable = false, nullable = false)
	@JsonProperty(access = Access.READ_ONLY)
	@CreatedDate
	private Instant createdDate;

	@Column(nullable = false)
	@JsonProperty(access = Access.READ_ONLY)
	@LastModifiedDate
	private Instant lastModifiedDate;
	
	
	public BaseEntity(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != getClass())
			return false;
		var be = (BaseEntity)o;
		return Objects.equals(uuid, be.uuid);
	}
}
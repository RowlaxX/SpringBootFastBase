package fr.rowlaxx.springbase.core;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JsonProperty(access = Access.READ_ONLY)
	private UUID uuid;
	
	@Column(updatable = false, unique = true)
	@JsonProperty(access = Access.READ_ONLY)
	private final Instant createdDate = Instant.now();
	
}

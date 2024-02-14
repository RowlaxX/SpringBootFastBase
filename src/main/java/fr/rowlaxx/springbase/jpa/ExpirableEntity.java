package fr.rowlaxx.springbase.jpa;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

import fr.rowlaxx.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class ExpirableEntity extends BaseEntity {

	@Column(nullable = false)
	private Instant expirationDate = Instant.MAX;
	
	public void setExpirationPeriod(TemporalAmount duration) {
		if (duration == null)
			expirationDate = Instant.MAX;
		else
			expirationDate = getCreatedDate().plus(duration);
	}
	
	public boolean isExpired() {
		return Instant.now().isAfter(expirationDate);
	}
	
	public Duration getRemaningDuration() {
		var now = Instant.now();
		var expire = getExpirationDate();
		
		if (now.isAfter(expire))
			return Duration.ZERO;
		return Utils.substract(expire, now);
	}
	
	public Duration getElapsedDuration() {
		var now = Instant.now();
		var created = getCreatedDate();
		return Utils.substract(now, created);
	}
}

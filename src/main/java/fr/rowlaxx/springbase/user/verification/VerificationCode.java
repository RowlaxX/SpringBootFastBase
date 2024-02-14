package fr.rowlaxx.springbase.user.verification;

import java.util.Random;

import fr.rowlaxx.springbase.jpa.ExpirableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class VerificationCode extends ExpirableEntity {
	private static final Random RANDOM = new Random();
	private static final int MAX_ATTEMPT = 3;
	
	@Column(nullable = false)
	private int value = RANDOM.nextInt(1_000_000);
	
	@Column(nullable = false)
	private int attempt = 0;
	
	public void addOneAttempt() {
		attempt++;
	}
	
	public boolean isLocked() {
		return attempt >= MAX_ATTEMPT;
	}
}

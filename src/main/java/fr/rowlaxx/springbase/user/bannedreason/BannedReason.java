package fr.rowlaxx.springbase.user.bannedreason;

import fr.rowlaxx.springbase.jpa.ExpirableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BannedReason extends ExpirableEntity {
		
	@Column(nullable = false, length = 512)
	private String description;
	
	public String getFullDescription() {
		if (getExpirationDate() == null)
			return "This user has been banned for the following reason : " + description;
		return "This user is still banned for " + getRemaningDuration().toHours() + " hours for the following reason : " + description;
	}
}

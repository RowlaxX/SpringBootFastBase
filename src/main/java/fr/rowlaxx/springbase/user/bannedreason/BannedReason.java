package fr.rowlaxx.springbase.user.bannedreason;

import fr.rowlaxx.springbase.core.ExpirableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@ToString(callSuper = true)
public class BannedReason extends ExpirableEntity {
		
	@Column(nullable = false, length = 512)
	private String description;
	
	public String getFullDescription() {
		if (getExpirationDate() == null)
			return "This user has been banned for the following reason : " + description;
		return "This user is still banned for " + getRemaningDuration().toHours() + " hours for the following reason : " + description;
	}
}

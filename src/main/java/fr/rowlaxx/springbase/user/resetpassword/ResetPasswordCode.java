package fr.rowlaxx.springbase.user.resetpassword;

import java.util.UUID;

import fr.rowlaxx.springbase.jpa.ExpirableEntity;
import fr.rowlaxx.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ResetPasswordCode extends ExpirableEntity {
	
	@Column(nullable = false, length = 64, unique = true)
	private String value = Utils.generateRandomDataHex(32);
	
	@Column(nullable = false, unique = true)
	private UUID userUuid;
}

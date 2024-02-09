package fr.rowlaxx.springbase.user.resetpassword;

import java.util.UUID;

import fr.rowlaxx.springbase.core.ExpirableEntity;
import fr.rowlaxx.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
public class ResetPasswordCode extends ExpirableEntity {
	
	@Column(nullable = false, length = 64, unique = true)
	private String value = Utils.generateRandomDataHex(32);
	
	@Column(nullable = false, unique = true)
	private UUID userUuid;
}

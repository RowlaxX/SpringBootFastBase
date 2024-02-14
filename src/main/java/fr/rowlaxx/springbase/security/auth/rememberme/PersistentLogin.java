package fr.rowlaxx.springbase.security.auth.rememberme;

import java.util.UUID;

import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import fr.rowlaxx.springbase.jpa.ExpirableEntity;
import fr.rowlaxx.utils.Utils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PersistentLogin extends ExpirableEntity {
	
	@Column(nullable = false)
	private UUID userUuid;
	
	@Column(nullable = false, length = 32)
	private String salt = Utils.generateRandomDataHex(16);
	
	@Column(nullable = false, length = 64)
	private String secret = Utils.generateBase64SecretKey256Bits();
	
	
	
	
	public String encryptPassword(String password) {
		//Hashing
		var hashed = Sha512DigestUtils.sha(password);
		//Encrypting
		var aes = new AesBytesEncryptor(secret, salt);
		var encrypted = aes.encrypt(hashed);
		//Encoding
		return HexUtils.toHexString(encrypted);
	}
	
	public boolean isValid(String userPassword, String encryptedPassword) {
		return encryptPassword(userPassword).equals(encryptedPassword);
	}
}

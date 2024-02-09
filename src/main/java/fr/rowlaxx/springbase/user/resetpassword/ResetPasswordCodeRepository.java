package fr.rowlaxx.springbase.user.resetpassword;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordCodeRepository extends JpaRepository<ResetPasswordCode, UUID> {

	ResetPasswordCode findByValue(String value);
	
}

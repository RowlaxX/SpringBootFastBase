package fr.rowlaxx.springbase.user.resetpassword;

import org.springframework.stereotype.Repository;

import fr.rowlaxx.springbase.jpa.BaseJpaRepository;

@Repository
public interface ResetPasswordCodeRepository extends BaseJpaRepository<ResetPasswordCode> {

	ResetPasswordCode findByValue(String value);
	
}

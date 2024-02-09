package fr.rowlaxx.springbase.security.auth.rememberme;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersistentLoginRepository extends JpaRepository<PersistentLogin, UUID> {

	PersistentLogin findByUuid(UUID uuid);
	
}

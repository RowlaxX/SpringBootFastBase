package fr.rowlaxx.springbase.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface UserJpaRepository<T extends BaseUser> extends JpaRepository<T, UUID> {

	@Query("SELECT u.password FROM User u WHERE u.uuid = :uuid")
    String findPasswordByUuid(@Param("uuid") UUID uuid);
	
	<S extends T> S findByUuid(UUID uuid);
	<S extends T> S findByEmail(String email);
	boolean existsByEmail(String email);
	
}

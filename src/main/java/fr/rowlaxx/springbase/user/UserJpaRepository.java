package fr.rowlaxx.springbase.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import fr.rowlaxx.springbase.jpa.BaseJpaRepository;

@NoRepositoryBean
public interface UserJpaRepository<T extends BaseUser> extends BaseJpaRepository<T> {

	@Query("SELECT u.password FROM User u WHERE u.uuid = :uuid")
    String findPasswordByUuid(@Param("uuid") UUID uuid);
	
	<S extends T> S findByEmail(String email);
	boolean existsByEmail(String email);
	
}

package fr.rowlaxx.springbase.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseEntity> extends JpaRepository<T, UUID> {

	<S extends T> S findByUuid(UUID uuid);
	boolean existsByUuid(UUID uuid);
	
}

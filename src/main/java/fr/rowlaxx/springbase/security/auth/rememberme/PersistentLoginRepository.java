package fr.rowlaxx.springbase.security.auth.rememberme;

import org.springframework.stereotype.Repository;

import fr.rowlaxx.springbase.jpa.BaseJpaRepository;

@Repository
public interface PersistentLoginRepository extends BaseJpaRepository<PersistentLogin> {

}

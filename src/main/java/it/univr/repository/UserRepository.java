package it.univr.repository;

import it.univr.model.Utente;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Utente, Long> {
    Utente findByEmail(String email);
}
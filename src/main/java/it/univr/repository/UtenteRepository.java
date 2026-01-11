package it.univr.repository;

import it.univr.model.Utente;
import org.springframework.data.repository.CrudRepository;

public interface UtenteRepository extends CrudRepository<Utente, Long> {
    Utente findByEmail(String email);
    Utente findByUsername(String username);
}
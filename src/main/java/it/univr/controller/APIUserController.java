package it.univr.controller;

import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController //
@RequestMapping("/users")
public class APIUserController {

    @Autowired
    private UtenteRepository utenteRepository;

    // SCENARIO 1: Registrazione
    @PostMapping
    public ResponseEntity<?> register(@RequestBody Utente user) {
        if (utenteRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        Utente savedUser = utenteRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // SCENARIO 2: Login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Utente credentials) {
        Utente user = utenteRepository.findByEmail(credentials.getEmail());

        if (user != null && user.getPassword().equals(credentials.getPassword())) {
            if ("ATTIVO".equals(user.getStatus())) {
                return ResponseEntity.ok("Login Successful. Role: " + user.getRole());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not active");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    // SCENARIO 3: Modifica (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Utente userDetails) {
        Optional<Utente> userOpt = utenteRepository.findById(id);

        if (userOpt.isPresent()) {
            Utente user = userOpt.get();
            user.setRole(userDetails.getRole());
            user.setStatus(Status.ATTIVO);
            return ResponseEntity.ok(utenteRepository.save(user));
        } else {
            return ResponseEntity.notFound().build(); // Ritorna 404 senza eccezioni
        }
    }

    // SCENARIO 4: Eliminazione
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (utenteRepository.existsById(id)) {
            utenteRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping
    public Iterable<Utente> getAllUsers() {
        return utenteRepository.findAll();
    }
}
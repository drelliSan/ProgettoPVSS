package it.univr.controller;

import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class APIUserController {

    @Autowired // Injection del repository (Lab 03, Slide 31)
    private UtenteRepository utenteRepository;

    // Funzionalità: Creazione account utente
    // Utilizza POST come da best practice REST per creare risorse (Lab 03, Slide 8)
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody Utente newUser) { // @RequestBody prende l'oggetto dal JSON (Lab 03, Slide 32)

        // Controllo duplicati (Logica di business basata sui dati forniti)
        if (utenteRepository.findByEmail(newUser.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email già registrata");
        }
        if (utenteRepository.findByUsername(newUser.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username già in uso");
        }

        // Impostiamo i default se mancanti
        if (newUser.getStatus() == null) newUser.setStatus(Status.ATTIVO);
        if (newUser.getRole() == null) newUser.setRole("USER");

        utenteRepository.save(newUser);

        // Ritorna 201 Created (Lab 03, Slide 12)
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // Funzionalità: Autenticazione Utenti
    // REST è Stateless, quindi non usiamo Session. Ritorniamo l'utente se successo, 401 se fallito.
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credentials) {
        String emailOrUser = credentials.get("username");
        String password = credentials.get("password");

        Utente user = utenteRepository.findByEmail(emailOrUser);
        if (user == null) {
            user = utenteRepository.findByUsername(emailOrUser);
        }

        if (user != null && user.getPassword().equals(password)) {
            if (Status.ATTIVO.equals(user.getStatus())) {
                // Ritorna 200 OK con i dati dell'utente (Lab 03, Slide 12)
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account non attivo");
            }
        }

        // Ritorna Client Error se credenziali errate (Lab 03, Slide 14)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali non valide");
    }
}
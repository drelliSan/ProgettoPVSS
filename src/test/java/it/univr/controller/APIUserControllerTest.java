package it.univr.controller;

import it.univr.controller.APIUserController;
import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APIUserControllerTest {

    @Mock
    private UtenteRepository utenteRepository;

    @InjectMocks
    private APIUserController apiUserController;

    private Utente testUser;

    @BeforeEach
    void setUp() {
        // Setup di un utente standard per i test
        testUser = new Utente("Mario", "Rossi", "mario@email.com", "mariorossi", "password123");
        testUser.setStatus(Status.ATTIVO);
        testUser.setRole("USER");
    }

    // --- TEST REGISTER ---

    @Test
    void register_ShouldReturnCreated_WhenValid() {
        // Simuliamo che non esistano duplicati
        when(utenteRepository.findByEmail(anyString())).thenReturn(null);
        when(utenteRepository.findByUsername(anyString())).thenReturn(null);
        when(utenteRepository.save(any(Utente.class))).thenReturn(testUser);

        // Creiamo un nuovo utente (senza status/ruolo per testare i default)
        Utente newUser = new Utente();
        newUser.setEmail("new@email.com");
        newUser.setUsername("newuser");
        newUser.setPassword("pass");

        ResponseEntity<Object> response = apiUserController.register(newUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Utente body = (Utente) response.getBody();
        assertNotNull(body);
        // Verifica che i default siano stati settati
        assertEquals(Status.ATTIVO, body.getStatus());
        assertEquals("USER", body.getRole());

        verify(utenteRepository).save(newUser);
    }

    @Test
    void register_ShouldReturnConflict_WhenEmailExists() {
        // Simuliamo che l'email esista già
        when(utenteRepository.findByEmail("existing@email.com")).thenReturn(testUser);

        Utente newUser = new Utente();
        newUser.setEmail("existing@email.com");

        ResponseEntity<Object> response = apiUserController.register(newUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email già registrata", response.getBody());

        // Verifica che NON venga chiamato save
        verify(utenteRepository, never()).save(any());
    }

    @Test
    void register_ShouldReturnConflict_WhenUsernameExists() {
        when(utenteRepository.findByEmail(anyString())).thenReturn(null);
        // Simuliamo che lo username esista già
        when(utenteRepository.findByUsername("existingUser")).thenReturn(testUser);

        Utente newUser = new Utente();
        newUser.setEmail("new@email.com");
        newUser.setUsername("existingUser");

        ResponseEntity<Object> response = apiUserController.register(newUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username già in uso", response.getBody());
    }

    // --- TEST LOGIN ---

    @Test
    void login_ShouldReturnOk_WhenCredentialsValidByEmail() {
        // Test Login con Email
        when(utenteRepository.findByEmail("mario@email.com")).thenReturn(testUser);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "mario@email.com");
        credentials.put("password", "password123");

        ResponseEntity<Object> response = apiUserController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void login_ShouldReturnOk_WhenCredentialsValidByUsername() {
        // Test Login con Username (Branch coverage: findByEmail returns null)
        when(utenteRepository.findByEmail("mariorossi")).thenReturn(null);
        when(utenteRepository.findByUsername("mariorossi")).thenReturn(testUser);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "mariorossi");
        credentials.put("password", "password123");

        ResponseEntity<Object> response = apiUserController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenPasswordWrong() {
        when(utenteRepository.findByEmail("mario@email.com")).thenReturn(testUser);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "mario@email.com");
        credentials.put("password", "WRONG_PASSWORD");

        ResponseEntity<Object> response = apiUserController.login(credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenziali non valide", response.getBody());
    }

    @Test
    void login_ShouldReturnForbidden_WhenUserInactive() {
        // Utente esiste, password giusta, ma INATTIVO
        testUser.setStatus(Status.INATTIVO);
        when(utenteRepository.findByEmail("mario@email.com")).thenReturn(testUser);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "mario@email.com");
        credentials.put("password", "password123");

        ResponseEntity<Object> response = apiUserController.login(credentials);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Account non attivo", response.getBody());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenUserNotFound() {
        // Né email né username trovati
        when(utenteRepository.findByEmail(anyString())).thenReturn(null);
        when(utenteRepository.findByUsername(anyString())).thenReturn(null);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "ghost");
        credentials.put("password", "pass");

        ResponseEntity<Object> response = apiUserController.login(credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
package it.univr.controller;

import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebAuthControllerTest {

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private WebAuthController webAuthController;

    private Utente testUser;

    @BeforeEach
    void setUp() {
        testUser = new Utente("Mario", "Rossi", "mario@email.com", "mario", "pass");
        testUser.setStatus(Status.ATTIVO);
    }

    @Test
    void loginPage_ShouldReturnLoginView() {
        String view = webAuthController.loginPage(model);
        assertEquals("login", view);
    }

    @Test
    void doLogin_Success_WithEmail() {
        when(utenteRepository.findByEmail("mario@email.com")).thenReturn(testUser);

        String view = webAuthController.doLogin("mario@email.com", "pass", session, model);

        assertEquals("redirect:/dashboard", view);
        verify(session).setAttribute(eq("currentUser"), any(Utente.class));
    }

    @Test
    void doLogin_Success_WithUsername() {
        when(utenteRepository.findByEmail("mario")).thenReturn(null);
        when(utenteRepository.findByUsername("mario")).thenReturn(testUser);

        String view = webAuthController.doLogin("mario", "pass", session, model);

        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void doLogin_Fail_WrongPassword() {
        when(utenteRepository.findByEmail("mario@email.com")).thenReturn(testUser);

        String view = webAuthController.doLogin("mario@email.com", "wrongpass", session, model);

        assertEquals("login", view);
        verify(model).addAttribute(eq("error"), anyString());
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void doLogin_Fail_InactiveUser() {
        testUser.setStatus(Status.INATTIVO);
        when(utenteRepository.findByEmail("mario@email.com")).thenReturn(testUser);

        String view = webAuthController.doLogin("mario@email.com", "pass", session, model);

        assertEquals("login", view);
        verify(model).addAttribute(eq("error"), contains("non attivo"));
    }

    @Test
    void logout_ShouldInvalidateSession() {
        String view = webAuthController.logout(session);
        verify(session).invalidate();
        assertEquals("redirect:/login", view);
    }

    @Test
    void doRegister_Fail_EmailExists() {
        when(utenteRepository.findByEmail(anyString())).thenReturn(testUser);

        String view = webAuthController.doRegister("A", "B", "user", "email", "pass", model);

        assertEquals("register", view);
        verify(model).addAttribute(eq("error"), contains("Email già registrata"));
    }

    @Test
    void doRegister_Success() {
        when(utenteRepository.findByEmail(anyString())).thenReturn(null);
        when(utenteRepository.findByUsername(anyString())).thenReturn(null);

        String view = webAuthController.doRegister("A", "B", "newuser", "new@mail.com", "pass", model);

        assertEquals("redirect:/login?success=Registrazione completata! Attendi l'attivazione da parte dell'amministratore.", view);
        verify(utenteRepository).save(any(Utente.class));
    }
}
package it.univr.controller;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebMainControllerTest {

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private WebMainController webMainController;

    private Utente sessionUser;

    @BeforeEach
    void setUp() {
        sessionUser = new Utente("Mario", "Rossi", "mario@email.com", "mario", "oldPass");
        sessionUser.setId(1L);
    }

    @Test
    void home_ShouldReturnIndex() {
        when(utenteRepository.count()).thenReturn(1L);

        String view = webMainController.home();

        assertEquals("index", view);
    }

    @Test
    void dashboard_ShouldRedirectToLogin_WhenNotLogged() {
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = webMainController.dashboard(session, model);

        assertEquals("redirect:/login", view);
        verifyNoInteractions(model);
    }

    @Test
    void dashboard_ShouldShowDashboard_WhenLogged() {
        when(session.getAttribute("currentUser")).thenReturn(sessionUser);

        String view = webMainController.dashboard(session, model);

        assertEquals("dashboard", view);
        verify(model).addAttribute("user", sessionUser);
    }

    @Test
    void showProfileSettings_ShouldRedirect_WhenNotLogged() {
        when(session.getAttribute("currentUser")).thenReturn(null);

        String view = webMainController.showProfileSettings(session, model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void showProfileSettings_ShouldShowForm_WhenLogged() {
        when(session.getAttribute("currentUser")).thenReturn(sessionUser);

        String view = webMainController.showProfileSettings(session, model);

        assertEquals("profile-settings", view);
        verify(model).addAttribute("utenteForm", sessionUser);
    }

    @Test
    void updateProfile_ShouldRedirect_WhenNotLogged() {
        when(session.getAttribute("currentUser")).thenReturn(null);
        Utente formData = new Utente();

        String view = webMainController.updateProfile(formData, session);

        assertEquals("redirect:/login", view);
        verify(utenteRepository, never()).save(any());
    }

    @Test
    void updateProfile_ShouldUpdateDataAndRedirect_WhenLogged() {
        when(session.getAttribute("currentUser")).thenReturn(sessionUser);

        Utente formData = new Utente();
        formData.setFirstName("Luigi");
        formData.setLastName("Verdi");
        formData.setEmail("luigi@email.com");
        formData.setUsername("luigi");
        formData.setPassword("newSecretPass");

        String view = webMainController.updateProfile(formData, session);

        assertEquals("redirect:/dashboard", view);

        assertEquals("Luigi", sessionUser.getFirstName());
        assertEquals("newSecretPass", sessionUser.getPassword());

        verify(utenteRepository).save(sessionUser);
        verify(session).setAttribute("currentUser", sessionUser);
    }

    @Test
    void updateProfile_ShouldNotChangePassword_WhenEmpty() {
        when(session.getAttribute("currentUser")).thenReturn(sessionUser);

        Utente formData = new Utente();
        formData.setFirstName("Mario");
        formData.setLastName("Rossi");
        formData.setEmail("mario@email.com");
        formData.setUsername("mario");
        formData.setPassword("");

        webMainController.updateProfile(formData, session);

        assertEquals("oldPass", sessionUser.getPassword());

        verify(utenteRepository).save(sessionUser);
    }
}
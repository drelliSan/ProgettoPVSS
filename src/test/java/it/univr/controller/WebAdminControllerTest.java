package it.univr.controller;

import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebAdminControllerTest {

    @Mock
    private UtenteRepository utenteRepository;

    @Mock
    private Model model;

    @InjectMocks
    private WebAdminController webAdminController;

    private Utente testUser;

    @BeforeEach
    void setUp() {
        testUser = new Utente("Mario", "Rossi", "mario@email.com", "mario", "pass");
        testUser.setId(1L);
        testUser.setRole("USER");
        testUser.setStatus(Status.ATTIVO);
    }

    @Test
    void listUsers_ShouldAddUsersToModel() {
        when(utenteRepository.findAll()).thenReturn(Collections.singletonList(testUser));

        String viewName = webAdminController.listUsers(model);

        assertEquals("admin-users", viewName);
        verify(model).addAttribute(eq("users"), any());
    }

    @Test
    void toggleRole_ShouldChangeUserToAdmin_WhenUserExists() {
        when(utenteRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String viewName = webAdminController.toggleRole(1L);

        assertEquals("ADMIN", testUser.getRole());
        verify(utenteRepository).save(testUser);
        assertEquals("redirect:/admin/users", viewName);
    }

    @Test
    void toggleRole_ShouldChangeAdminToUser_WhenUserExists() {
        testUser.setRole("ADMIN");
        when(utenteRepository.findById(1L)).thenReturn(Optional.of(testUser));

        webAdminController.toggleRole(1L);

        assertEquals("USER", testUser.getRole());
    }

    @Test
    void toggleRole_ShouldDoNothing_WhenUserNotFound() {
        when(utenteRepository.findById(99L)).thenReturn(Optional.empty());

        webAdminController.toggleRole(99L);

        verify(utenteRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldCallDelete() {
        String viewName = webAdminController.deleteUser(1L);
        verify(utenteRepository).deleteById(1L);
        assertEquals("redirect:/admin/users", viewName);
    }

    @Test
    void changeStatus_ShouldToggleStatus() {
        when(utenteRepository.findById(1L)).thenReturn(Optional.of(testUser));
        webAdminController.changeStatus(1L);
        assertEquals(Status.INATTIVO, testUser.getStatus());

        webAdminController.changeStatus(1L);
        assertEquals(Status.ATTIVO, testUser.getStatus());
    }

    @Test
    void showEditUserForm_ShouldReturnEditView_WhenFound() {
        when(utenteRepository.findById(1L)).thenReturn(Optional.of(testUser));
        String view = webAdminController.showEditUserForm(1L, model);
        assertEquals("admin-user-edit", view);
    }

    @Test
    void showEditUserForm_ShouldRedirect_WhenNotFound() {
        when(utenteRepository.findById(99L)).thenReturn(Optional.empty());
        String view = webAdminController.showEditUserForm(99L, model);
        assertEquals("redirect:/admin/users", view);
    }

    @Test
    void updateUser_ShouldUpdateFields() {
        Utente formData = new Utente();
        formData.setId(1L);
        formData.setFirstName("Luigi");
        formData.setPassword("newpass");

        when(utenteRepository.findById(1L)).thenReturn(Optional.of(testUser));

        webAdminController.updateUser(formData);

        assertEquals("Luigi", testUser.getFirstName());
        assertEquals("newpass", testUser.getPassword());
        verify(utenteRepository).save(testUser);
    }
}
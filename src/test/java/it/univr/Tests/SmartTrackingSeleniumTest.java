package it.univr.Tests;

import it.univr.PageObjects.*;
import it.univr.model.Status; // Assicurati che l'enum sia importato correttamente
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SmartTrackingSeleniumTest extends BaseTest {

    @Autowired
    private UtenteRepository utenteRepository;

    @Before
    public void setupDatabase() {
        utenteRepository.deleteAll();

        // 1. Utente MARIO (User standard) - ATTIVO per poter fare Login
        Utente mario = new Utente();
        mario.setFirstName("Mario");
        mario.setLastName("Rossi");
        mario.setUsername("mariorossi");
        mario.setEmail("mario@test.com");
        mario.setRole("USER");
        mario.setStatus(Status.ATTIVO); // Importante per Scenario 2.2
        mario.setPassword("password123"); // Password in chiaro
        utenteRepository.save(mario);

        // 2. Utente ADMIN - ATTIVO
        Utente admin = new Utente();
        admin.setFirstName("Admin");
        admin.setLastName("Super");
        admin.setUsername("adminuser");
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");
        admin.setStatus(Status.ATTIVO); // FIX: Era mario.setStatus
        admin.setPassword("adminpass");
        utenteRepository.save(admin);

        // 3. Utente DA CANCELLARE (Scenario 2.5)
        Utente userToDelete = new Utente();
        userToDelete.setFirstName("Delete");
        userToDelete.setLastName("Me");
        userToDelete.setUsername("userToDelete");
        userToDelete.setEmail("delete@test.com");
        userToDelete.setRole("USER");
        userToDelete.setStatus(Status.ATTIVO);
        userToDelete.setPassword("pass");
        utenteRepository.save(userToDelete);

        // 4. Utente DA PROMUOVERE (Scenario 2.4)
        Utente userToPromote = new Utente();
        userToPromote.setFirstName("Promote");
        userToPromote.setLastName("Me");
        userToPromote.setUsername("userToPromote");
        userToPromote.setEmail("promote@test.com");
        userToPromote.setRole("USER");
        userToPromote.setStatus(Status.ATTIVO);
        userToPromote.setPassword("pass");
        utenteRepository.save(userToPromote);
    }

    // --- Scenario 2.1: Registrazione ---
    @Test
    public void testRegistrationSuccess() {
        driver.get("http://localhost:8080/");
        HomePage home = new HomePage(driver);
        RegisterPage register = home.clickRegister();

        register.registerUser("Luigi", "Verdi", "luigiverdi", "luigi@test.com", "password123");

        assertFalse("Errore visualizzato durante la registrazione", register.isErrorDisplayed());
    }

    @Test
    public void testRegistrationFailureDuplicate() {
        driver.get("http://localhost:8080/register");
        RegisterPage register = new RegisterPage(driver);

        register.registerUser("Mario", "Rossi", "mariorossi", "mario@test.com", "password123");

        assertTrue("Il messaggio di errore dovrebbe essere visibile per duplicati", register.isErrorDisplayed());
    }

    // --- Scenario 2.2: Login ---
    @Test
    public void testLoginSuccess() {
        driver.get("http://localhost:8080/login");
        LoginPage login = new LoginPage(driver);

        DashboardPage dashboard = login.loginAs("mario@test.com", "password123");

        assertTrue(dashboard.getWelcomeMessage().contains("Benvenuto"));
    }

    @Test
    public void testLoginFailure() {
        driver.get("http://localhost:8080/login");
        LoginPage login = new LoginPage(driver);

        login.loginAs("mariorossi", "wrongpass"); // Test anche con username o email errata

        assertTrue("Dovrebbe apparire un alert di errore", login.isErrorPresent());
    }

    // --- Scenario 2.3: Profilo ---
    @Test
    public void testEditProfile() {
        driver.get("http://localhost:8080/login");
        LoginPage login = new LoginPage(driver);
        DashboardPage dashboard = login.loginAs("mario@test.com", "password123");

        ProfilePage profile = dashboard.goToProfile();
        profile.updateName("Luigi");
    }

    // --- Scenario 2.4 & 2.5: Admin Management ---
    @Test
    public void testAdminUserManagement() {
        driver.get("http://localhost:8080/login");
        LoginPage login = new LoginPage(driver);

        DashboardPage dashboard = login.loginAs("admin@test.com", "adminpass");

        AdminUsersPage adminPage = dashboard.goToAdminUsers();

        if (adminPage.isUserPresent("userToPromote")) {
            adminPage.changeRole("userToPromote");
        }

        if (adminPage.isUserPresent("userToDelete")) {
            adminPage.deleteUser("userToDelete");
            assertFalse("L'utente dovrebbe essere stato rimosso", adminPage.isUserPresent("userToDelete"));
        }
    }

    // --- Scenario 2.6: Provisioning Device ---
    @Test
    public void testDeviceProvisioning() {
        driver.get("http://localhost:8080/login");
        DashboardPage dashboard = new LoginPage(driver).loginAs("mario@test.com", "password123");

        ProvisionPage provision = dashboard.goToProvisioning();
        provision.clickGenerate();

        assertTrue("Il QR Code dovrebbe essere visibile dopo la generazione", provision.isQrDisplayed());
    }

    // --- Scenario 2.7: Associazione Device ---
    @Test
    public void testDeviceAssociationUI() {
        driver.get("http://localhost:8080/login");
        DashboardPage dash = new LoginPage(driver).loginAs("mario@test.com", "password123");

        DevicesPage devices = dash.goToMyDevices();
        AssociatePage associate = devices.clickAssociate();

        associate.enterMacAndConfirm("AA:BB:CC:11:22:33");

        assertTrue("Dovrebbe tornare alla lista devices", driver.getCurrentUrl().contains("/devices"));
    }
}
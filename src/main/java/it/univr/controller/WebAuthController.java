package it.univr.controller;

import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebAuthController {

    @Autowired
    private UtenteRepository utenteRepository;

    // Pagina di Login (Scenario 2)
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Accedi");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam("username") String email,
            @RequestParam("password") String password,
            HttpSession session, // <--- 1. Richiedi la sessione qui
            Model model) {

        Utente user = utenteRepository.findByEmail(email);

        // Piccolo fix: gestione null safe
        if(user == null){
            user = utenteRepository.findByUsername(email);
        }

        if (user != null && user.getPassword().equals(password)) {
            if (Status.ATTIVO.equals(user.getStatus())) {

                // 2. SALVA L'UTENTE NELLA SESSIONE
                // Questo è il "biscottino" che il server tiene in memoria
                session.setAttribute("currentUser", user);

                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Account non attivo. Attendi approvazione.");
                return "login";
            }
        }

        model.addAttribute("error", "Credenziali non valide");
        return "login";
    }

    // Aggiungi anche il Logout per pulire la sessione
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Distrugge la sessione
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Registrati");
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        // 1. Controllo unicità Email
        if (utenteRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email già registrata!");
            return "register";
        }

        // 2. Controllo unicità Username
        if (utenteRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Username già in uso, scegline un altro.");
            return "register";
        }

        // 3. Creazione nuovo utente con tutti i campi
        Utente newUser = new Utente(firstName, lastName, email, username, password);

        utenteRepository.save(newUser);

        return "redirect:/login?success=Registrazione completata! Attendi l'attivazione da parte dell'amministratore.";
    }
}
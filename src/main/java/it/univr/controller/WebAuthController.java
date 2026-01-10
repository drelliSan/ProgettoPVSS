package it.univr.controller;

import it.univr.model.Utente;
import it.univr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebAuthController {

    @Autowired
    private UserRepository userRepository;

    // Pagina di Login (Scenario 2)
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Accedi");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam("username") String email, // Nel form il campo name="username", ma contiene l'email
            @RequestParam("password") String password,
            Model model) {

        Utente user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            if ("ATTIVO".equals(user.getStatus())) {
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Account non attivo. Attendi approvazione.");
                return "login";
            }
        }

        model.addAttribute("error", "Credenziali non valide");
        return "login";
    }

    // Pagina di Registrazione (Scenario 1)
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Registrati");
        return "register";
    }

    // Gestione POST Registrazione (Scenario 1)
    @PostMapping("/register")
    public String doRegister(
            @RequestParam("username") String username, // mappato su firstName/lastName o campo dedicato
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email già registrata!");
            return "register";
        }

        // Creiamo l'utente (password andrebbe hashata in un sistema reale)
        // Assumiamo che User abbia un costruttore adatto o usiamo i setter
        Utente newUser = new Utente(username, "", email,email, password);
        userRepository.save(newUser);

        return "redirect:/login?success=Registrazione avvenuta con successo!";
    }
}
package it.univr.controller;

import it.univr.Utils;
import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class WebMainController {

    @Autowired
    private UtenteRepository utenteRepository;

    // Pagina Home (Index)
    @GetMapping("/")
    public String home() {
        Utils.createUser(utenteRepository);
        return "index";
    }

    // Dashboard (Scenario 2 post-login)
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Principal contiene l'utente loggato (grazie a Spring Security)
        String username = (principal != null) ? principal.getName() : "Ospite";

        model.addAttribute("title", "Dashboard");
        model.addAttribute("username", username);
        return "dashboard";
    }
}
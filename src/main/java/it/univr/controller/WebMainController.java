package it.univr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class WebMainController {

    // Pagina Home (Index)
    @GetMapping("/")
    public String home() {
        return "index"; // Manca il template index.html (te lo fornisco sotto)
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
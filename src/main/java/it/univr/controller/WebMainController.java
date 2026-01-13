package it.univr.controller;

import it.univr.Utils;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class WebMainController {

    @Autowired
    private UtenteRepository utenteRepository;

    // Pagina Home (Index)
    @GetMapping("/")
    public String home() {
        if(utenteRepository.count()==0) Utils.createUser(utenteRepository);
        return "index";
    }

    // Dashboard (Scenario 2 post-login)
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 1. Recupera l'utente dalla sessione
        Utente user = (Utente) session.getAttribute("currentUser");

        // 2. Se è null, vuol dire che non ha fatto login
        if (user == null) {
            return "redirect:/login";
        }

        // 3. Se c'è, mostra la pagina
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/profileSettings")
    public String showProfileSettings(HttpSession session, Model model) {
        Utente user = (Utente) session.getAttribute("currentUser");

        // Controllo di sicurezza
        if (user == null) {
            return "redirect:/login";
        }

        // Passiamo l'utente al modello per riempire i campi
        model.addAttribute("utenteForm", user);
        return "profile-settings";
    }

    @PostMapping("/profileSettings")
    public String updateProfile(@ModelAttribute("utenteForm") Utente formData, HttpSession session) {
        Utente sessionUser = (Utente) session.getAttribute("currentUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        sessionUser.setFirstName(formData.getFirstName());
        sessionUser.setLastName(formData.getLastName());
        sessionUser.setEmail(formData.getEmail());
        sessionUser.setUsername(formData.getUsername());

        if (formData.getPassword() != null && !formData.getPassword().isEmpty()) {
            sessionUser.setPassword(formData.getPassword());
        }

        utenteRepository.save(sessionUser);

        session.setAttribute("currentUser", sessionUser);

        return "redirect:/dashboard";
    }
}
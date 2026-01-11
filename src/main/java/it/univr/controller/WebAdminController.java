package it.univr.controller;

import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class WebAdminController {

    @Autowired
    private UtenteRepository utenteRepository;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", utenteRepository.findAll());
        model.addAttribute("title", "Gestione Utenti - SmartTracking");
        return "admin-users";
    }

    @PostMapping("/role/{id}")
    public String toggleRole(@PathVariable Long id) {
        Optional<Utente> userOpt = utenteRepository.findById(id);
        if (userOpt.isPresent()) {
            Utente user = userOpt.get();
            if ("ADMIN".equals(user.getRole())) user.setRole("USER");
            else  user.setRole("ADMIN");
            utenteRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        utenteRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/changeStatus/{id}")
    public String changeStatus(@PathVariable Long id) {
        Optional<Utente> userOpt = utenteRepository.findById(id);
        if (userOpt.isPresent()) {
            Utente user = userOpt.get();
            if(user.getStatus().equals(Status.INATTIVO)) user.setStatus(Status.ATTIVO);
            else user.setStatus(Status.INATTIVO);
            utenteRepository.save(user);
        }
        return "redirect:/admin/users";
    }
}
package it.univr.controller;

import it.univr.model.Utente;
import it.univr.repository.UserRepository;
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
    private UserRepository userRepository;

    // Lista utenti (Scenario 3)
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin-users";
    }

    // Cambia ruolo (Scenario 3)
    @PostMapping("/role/{id}")
    public String toggleRole(@PathVariable Long id) {
        Optional<Utente> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            Utente user = userOpt.get();
            // Logica toggle semplice: se ADMIN diventa USER, se USER diventa ADMIN
            if ("ADMIN".equals(user.getRole())) {
                user.setRole("USER");
            } else {
                user.setRole("ADMIN");
            }
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }

    // Elimina utente (Scenario 4)
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
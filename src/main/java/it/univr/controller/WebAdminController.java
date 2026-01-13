package it.univr.controller;

import it.univr.model.Status;
import it.univr.model.Utente;
import it.univr.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Optional<Utente> userOpt = utenteRepository.findById(id);

        if (userOpt.isPresent()) {
            model.addAttribute("userForm", userOpt.get());
            return "admin-user-edit"; // Nome del nuovo file HTML
        } else {
            return "redirect:/admin/users"; // Se l'ID non esiste, torna alla lista
        }
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("userForm") Utente formData) {
        Optional<Utente> userOpt = utenteRepository.findById(formData.getId());

        if (userOpt.isPresent()) {
            Utente existingUser = userOpt.get();

            // Aggiorniamo i dati anagrafici
            existingUser.setFirstName(formData.getFirstName());
            existingUser.setLastName(formData.getLastName());
            existingUser.setEmail(formData.getEmail());
            existingUser.setUsername(formData.getUsername());

            // L'admin può resettare la password. Se il campo è vuoto, manteniamo la vecchia.
            if (formData.getPassword() != null && !formData.getPassword().isEmpty()) {
                existingUser.setPassword(formData.getPassword());
            }

            utenteRepository.save(existingUser);
        }

        return "redirect:/admin/users";
    }
}
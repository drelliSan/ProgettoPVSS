package it.univr;

import it.univr.model.*;
import it.univr.repository.DeviceRepository;
import it.univr.repository.UtenteRepository;

import java.util.Arrays;

public class Utils {

    public static void createUser(UtenteRepository repo) {

        Utente admin = new Utente();
        admin.setEmail("admin@test.com");
        admin.setPassword("admin123");
        admin.setFirstName("Mario");
        admin.setLastName("Admin");
        admin.setRole("ADMIN");
        admin.setStatus(Status.ATTIVO);
        repo.save(admin);

        Utente inattivo = new Utente();
        inattivo.setEmail("inattivo@test.com");
        inattivo.setPassword("user123");
        inattivo.setFirstName("Luigi");
        inattivo.setLastName("Inattivo");
        inattivo.setRole("USER");
        inattivo.setStatus(Status.INATTIVO);
        repo.save(inattivo);

        Utente standard = new Utente();
        standard.setEmail("user@test.com");
        standard.setPassword("user123");
        standard.setFirstName("Anna");
        standard.setLastName("User");
        standard.setRole("USER");
        standard.setStatus(Status.ATTIVO);
        repo.save(standard);
    }

    public static void createDevice(DeviceRepository repo) {

        Device first = new Device("AA:BB:CC:11:22:33");
        first.setStatus(Status.INATTIVO);
        repo.save(first);

        Device second = new Device("AA:BB:CC:11:22:44");
        second.setStatus(Status.ATTIVO);
        repo.save(second);


    }

}

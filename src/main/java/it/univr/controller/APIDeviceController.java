package it.univr.controller;

import it.univr.model.Device;
import it.univr.model.Status;
import it.univr.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController //
@RequestMapping("/api/devices")
public class APIDeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    // SCENARIO 5: Registrazione Device
    @PostMapping
    public ResponseEntity<?> registerDevice(@RequestBody Device deviceData) {
        if (deviceRepository.findByMacAddress(deviceData.getMacAddress()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Device already exists");
        }
        Device savedDevice = deviceRepository.save(new Device(deviceData.getMacAddress()));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
    }

    // SCENARIO 6: Attivazione (Provisioning)
    @PutMapping("/{id}/provision")
    public ResponseEntity<String> provisionDevice(@PathVariable Long id) {
        Optional<Device> devOpt = deviceRepository.findById(id);

        if (devOpt.isPresent()) {
            Device device = devOpt.get();
            if (device.getStatus().equals(Status.INATTIVO)) {
                String newApiKey = UUID.randomUUID().toString();
                device.setApiKey(newApiKey);
                device.setStatus(Status.ATTIVO);
                deviceRepository.save(device);
                return ResponseEntity.ok(newApiKey);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Device already active");
            }
        }
        return ResponseEntity.notFound().build(); // Gestione manuale del 404
    }

    // SCENARIO 7: Invio Dati
    @PostMapping("/data")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestBody String sensorData) {

        Device device = deviceRepository.findByApiKey(apiKey);

        if (device != null && device.getStatus().equals(Status.ATTIVO)) {
            System.out.println("Data received: " + sensorData);
            return ResponseEntity.ok("Data received");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");
    }
}
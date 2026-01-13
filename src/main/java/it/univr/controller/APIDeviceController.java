package it.univr.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import it.univr.model.Device;
import it.univr.model.Status;
import it.univr.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
public class APIDeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    // Funzionalità: Provisioning nuovi devices (Pre-registrazione + QR)
    @PostMapping("/provision")
    public ResponseEntity<Object> provisionDevice() {
        Device device = new Device();
        device.setStatus(Status.INATTIVO);

        device = deviceRepository.save(device); // Salvataggio su DB (Lab 03, Slide 32)

        try {
            // Generazione QR Code (Logica mantenuta dal WebDeviceController fornito)
            String qrContent = String.valueOf(device.getId());
            String qrBase64 = generateQRCodeImage(qrContent);

            // Creiamo una risposta JSON con Device ID e Immagine QR
            Map<String, Object> response = new HashMap<>();
            response.put("device", device);
            response.put("qrCode", qrBase64);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore generazione QR");
        }
    }

    // Funzionalità: Associazione Device (da App Utente)
    // L'utente scansiona il QR (passa ID) e inserisce il MAC
    @PostMapping("/associate")
    public ResponseEntity<Object> associateDevice(@RequestParam Long deviceId, @RequestParam String macAddress) {
        Optional<Device> deviceOpt = deviceRepository.findById(deviceId); // (Lab 03, Slide 31)

        if (deviceOpt.isPresent()) {
            Device device = deviceOpt.get();
            device.setMacAddress(macAddress);
            device.setStatus(Status.ATTIVO); // Attiva il dispositivo

            deviceRepository.save(device);
            return ResponseEntity.ok(device);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device non trovato"); // (Lab 03, Slide 14)
        }
    }

    // Funzionalità: Autenticazione Device
    // Il dispositivo fisico chiama questa API per verificare se può trasmettere
    @PostMapping("/auth")
    public ResponseEntity<String> authenticateDevice(@RequestBody Map<String, String> payload) {
        // Simuliamo che il device invii ID e MAC per autenticarsi
        String idStr = payload.get("id");
        String mac = payload.get("macAddress");

        if (idStr != null && mac != null) {
            Long id = Long.parseLong(idStr);
            Optional<Device> deviceOpt = deviceRepository.findById(id);

            if (deviceOpt.isPresent()) {
                Device dev = deviceOpt.get();
                // Verifica corrispondenza MAC e che lo stato sia ATTIVO
                if (mac.equals(dev.getMacAddress()) && Status.ATTIVO.equals(dev.getStatus())) {
                    return ResponseEntity.ok("AUTHORIZED");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
    }

    // Metodo di utility per QR Code (preso dal tuo WebDeviceController)
    private String generateQRCodeImage(String text) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", bos);
        byte[] imageBytes = bos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
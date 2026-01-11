package it.univr.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import it.univr.model.Device;
import it.univr.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

@Controller
public class WebDeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    // Lista "I miei dispositivi"
    @GetMapping("/devices")
    public String listDevices(Model model) {
        // Qui dovresti filtrare per utente loggato in un'app reale
        // Per ora mostriamo tutti i device
        Iterable<Device> devices = deviceRepository.findAll();
        model.addAttribute("devices", devices);
        return "device-list";
    }

    // Pagina Provisioning (Scenario 5)
    @GetMapping("/devices/provision")
    public String provisionPage() {
        return "device-provision";
    }

    // Azione "Genera Dispositivo" (Scenario 5)
    @PostMapping("/devices/create")
    public String createDevice(Model model) {
        // 1. Crea il device nel DB
        String macAddress = UUID.randomUUID().toString(); // Simuliamo un MAC/ID univoco
        Device device = new Device(macAddress);
        device = deviceRepository.save(device);

        // 2. Genera QR Code con l'ID del device
        try {
            String qrContent = String.valueOf(device.getId()); // Il contenuto del QR è l'ID
            String qrBase64 = generateQRCodeImage(qrContent);

            model.addAttribute("device", device);
            model.addAttribute("qrCode", qrBase64);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/devices/provision?error";
        }

        return "device-provision";
    }

    // Helper per generare QR Code in Base64
    private String generateQRCodeImage(String text) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", bos);
        byte[] imageBytes = bos.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
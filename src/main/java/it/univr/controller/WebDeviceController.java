package it.univr.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import it.univr.Utils;
import it.univr.model.Device;
import it.univr.model.Status; // Assicurati di avere questo enum
import it.univr.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;

@Controller
public class WebDeviceController {

    @Autowired
    private DeviceRepository deviceRepository;

    @GetMapping("/devices")
    public String listDevices(Model model) {
        if(deviceRepository.count()==0) Utils.createDevice(deviceRepository);
        // Qui dovresti filtrare per utente loggato in un'app reale
        // Per ora mostriamo tutti i device
        Iterable<Device> devices = deviceRepository.findAll();
        model.addAttribute("devices", devices);
        return "device-list";
    }

    // Pagina Provisioning (Form inserimento MAC)
    @GetMapping("/devices/provision")
    public String provisionPage(Model model) {
        model.addAttribute("title", "Nuovo Dispositivo");
        return "device-provision";
    }

    // Scenario 5: Registrazione Nuovo Device (Pre-Provisioning)
    @PostMapping("/devices/create")
    public String createDevice(Model model) {

        Device device = new Device();
        device.setStatus(Status.INATTIVO);

        device = deviceRepository.save(device);

        try {
            String qrContent = String.valueOf(device.getId());
            String qrBase64 = generateQRCodeImage(qrContent);

            model.addAttribute("device", device);
            model.addAttribute("qrCode", qrBase64);
            model.addAttribute("successMessage", "Dispositivo pre-registrato con successo! Scansiona il QR Code.");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/devices/provision?error";
        }

        return "device-provision";
    }

    private String generateQRCodeImage(String text) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", bos);
        byte[] imageBytes = bos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
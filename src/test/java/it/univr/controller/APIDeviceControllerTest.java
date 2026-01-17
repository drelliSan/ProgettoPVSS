package it.univr.controller;

import it.univr.controller.APIDeviceController;
import it.univr.model.Device;
import it.univr.model.Status;
import it.univr.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class APIDeviceControllerTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private APIDeviceController apiDeviceController;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        // Setup base: un device con ID 1
        testDevice = new Device();
        testDevice.setId(1L);
        testDevice.setStatus(Status.INATTIVO);
    }

    // --- TEST PROVISIONING (POST /provision) ---

    @Test
    void provisionDevice_ShouldReturnCreatedAndQrCode() {
        // Quando salviamo, ritorniamo il device con ID 1
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);

        ResponseEntity<Object> response = apiDeviceController.provisionDevice();

        // Verifica Status Code 201 CREATED
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verifica Body (deve essere una Mappa con device e qrCode)
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("device"));
        assertTrue(body.containsKey("qrCode"));

        // Verifica che il QR code sia una stringa (Base64) non vuota
        String qrCode = (String) body.get("qrCode");
        assertNotNull(qrCode);
        assertFalse(qrCode.isEmpty());

        verify(deviceRepository).save(any(Device.class));
    }

    // --- TEST ASSOCIAZIONE (POST /associate) ---

    @Test
    void associateDevice_ShouldActivateDevice_WhenFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        ResponseEntity<Object> response = apiDeviceController.associateDevice(1L, "AA:BB:CC:DD:EE:FF");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verifica cambio stato e MAC
        Device updatedDevice = (Device) response.getBody();
        assertEquals(Status.ATTIVO, updatedDevice.getStatus());
        assertEquals("AA:BB:CC:DD:EE:FF", updatedDevice.getMacAddress());

        verify(deviceRepository).save(testDevice);
    }

    @Test
    void associateDevice_ShouldReturnNotFound_WhenDeviceMissing() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = apiDeviceController.associateDevice(99L, "AA:BB:CC:DD:EE:FF");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Device non trovato", response.getBody());

        verify(deviceRepository, never()).save(any());
    }

    // --- TEST AUTENTICAZIONE (POST /auth) ---

    @Test
    void authenticateDevice_ShouldAuthorize_WhenValid() {
        // Prepariamo un device ATTIVO con MAC corretto nel DB
        testDevice.setMacAddress("AA:BB:CC:DD:EE:FF");
        testDevice.setStatus(Status.ATTIVO);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // Prepariamo il payload della richiesta
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "1");
        payload.put("macAddress", "AA:BB:CC:DD:EE:FF");

        ResponseEntity<String> response = apiDeviceController.authenticateDevice(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("AUTHORIZED", response.getBody());
    }

    @Test
    void authenticateDevice_ShouldFail_WhenMacMismatch() {
        // Device nel DB ha MAC "AA..."
        testDevice.setMacAddress("AA:BB:CC:DD:EE:FF");
        testDevice.setStatus(Status.ATTIVO);
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // Richiesta con MAC errato "XX..."
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "1");
        payload.put("macAddress", "XX:XX:XX:XX:XX:XX");

        ResponseEntity<String> response = apiDeviceController.authenticateDevice(payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void authenticateDevice_ShouldFail_WhenStatusInactive() {
        // Device nel DB ha MAC giusto ma stato INATTIVO
        testDevice.setMacAddress("AA:BB:CC:DD:EE:FF");
        testDevice.setStatus(Status.INATTIVO); // <--- IMPORTANTE
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        Map<String, String> payload = new HashMap<>();
        payload.put("id", "1");
        payload.put("macAddress", "AA:BB:CC:DD:EE:FF");

        ResponseEntity<String> response = apiDeviceController.authenticateDevice(payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void authenticateDevice_ShouldFail_WhenDeviceNotFound() {
        when(deviceRepository.findById(99L)).thenReturn(Optional.empty());

        Map<String, String> payload = new HashMap<>();
        payload.put("id", "99");
        payload.put("macAddress", "AA:BB:CC:DD:EE:FF");

        ResponseEntity<String> response = apiDeviceController.authenticateDevice(payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void authenticateDevice_ShouldFail_WhenPayloadInvalid() {
        // Payload senza ID o senza MAC
        Map<String, String> payload = new HashMap<>();
        payload.put("id", "1");
        // manca macAddress

        ResponseEntity<String> response = apiDeviceController.authenticateDevice(payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(deviceRepository, never()).findById(any());
    }
}
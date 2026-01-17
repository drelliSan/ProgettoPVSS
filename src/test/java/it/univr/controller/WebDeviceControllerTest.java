package it.univr.controller;

import it.univr.model.Device;
import it.univr.model.Status;
import it.univr.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebDeviceControllerTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private Model model;

    @InjectMocks
    private WebDeviceController webDeviceController;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = new Device();
        testDevice.setId(1L);
        testDevice.setStatus(Status.INATTIVO);
    }

    @Test
    void listDevices_ShouldReturnListView() {
        when(deviceRepository.count()).thenReturn(1L);
        when(deviceRepository.findAll()).thenReturn(Collections.singletonList(testDevice));

        String view = webDeviceController.listDevices(model);

        assertEquals("device-list", view);
        verify(model).addAttribute(eq("devices"), any());
    }

    @Test
    void createDevice_ShouldGenerateQRAndSave() {
        when(deviceRepository.save(any(Device.class))).thenAnswer(i -> {
            Device d = i.getArgument(0);
            d.setId(100L);
            return d;
        });

        String view = webDeviceController.createDevice(model);

        assertEquals("device-provision", view);
        verify(model).addAttribute(eq("qrCode"), anyString());
        verify(model).addAttribute(eq("successMessage"), anyString());
    }

    @Test
    void toggleDeviceStatus_ShouldSwitchStatus() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        webDeviceController.toggleDeviceStatus(1L);

        assertEquals(Status.ATTIVO, testDevice.getStatus());
        verify(deviceRepository).save(testDevice);

        webDeviceController.toggleDeviceStatus(1L);
        assertEquals(Status.INATTIVO, testDevice.getStatus());
    }

    @Test
    void showAssociatePage_ShouldReturnView_WhenFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        String view = webDeviceController.showAssociatePage(1L, model);

        assertEquals("device-associate", view);
    }

    @Test
    void performAssociation_ShouldSetMacAndActivate() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        String view = webDeviceController.performAssociation(1L, "AA:BB:CC:DD:EE:FF");

        assertEquals("redirect:/devices", view);
        assertEquals("AA:BB:CC:DD:EE:FF", testDevice.getMacAddress());
        assertEquals(Status.ATTIVO, testDevice.getStatus());
        verify(deviceRepository).save(testDevice);
    }
}
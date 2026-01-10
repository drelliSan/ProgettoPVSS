package it.univr.repository;

import it.univr.model.Device;
import org.springframework.data.repository.CrudRepository;

public interface DeviceRepository extends CrudRepository<Device, Long> {
    Device findByMacAddress(String macAddress);
    Device findByApiKey(String apiKey);
}
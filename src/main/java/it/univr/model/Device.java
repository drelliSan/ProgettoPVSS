package it.univr.model;

import jakarta.persistence.*;

@Entity
public class Device {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String macAddress;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String apiKey;

    public Device() {}

    public Device(String macAddress) {
        this.macAddress = macAddress;
        this.status = Status.INATTIVO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
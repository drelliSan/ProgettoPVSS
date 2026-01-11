package it.univr.model;

import jakarta.persistence.*;

@Entity // [cite: 330]
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
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getMacAddress() { return macAddress; }
}
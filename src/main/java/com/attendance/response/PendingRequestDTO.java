package com.attendance.response;

import java.time.LocalDate;

public class PendingRequestDTO {
    private Long id;
    private Long employeeId;
    private LocalDate date;
    private String type;
    private String status;

    public PendingRequestDTO(Long id, Long employeeId, LocalDate date, String type, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDate getDate() { return date; }
    public String getType() { return type; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
}

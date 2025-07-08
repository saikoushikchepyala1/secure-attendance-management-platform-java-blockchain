package com.attendance.response;

import java.time.LocalDate;

public class RequestDTO {

    private Long id;
    private Long employeeId;
    private LocalDate date;
    private String type;
    private String status;

    public RequestDTO(Long id, Long employeeId, LocalDate date, String type, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.type = type;
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }


}

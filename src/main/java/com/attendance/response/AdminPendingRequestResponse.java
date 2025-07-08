package com.attendance.response;

public class AdminPendingRequestResponse {
    private Long id;
    private Long employeeId;
    private String type;
    private String date;
    private String status;

    public AdminPendingRequestResponse(Long id, Long empId, String type, String date, String status) {
        this.id = id;
        this.employeeId = empId;
        this.type = type;
        this.date = date;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getType() { return type; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}

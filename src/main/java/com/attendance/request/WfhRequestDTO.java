package com.attendance.request;

public class WfhRequestDTO {
    private Long employeeId;
    private String date; // Format: YYYY-MM-DD

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}

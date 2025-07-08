package com.attendance.response;

public class DashboardStatsResponse {
    private String name;
    private Long employeeId;
    private String email;
    private long totalDays;
    private long daysPresent;
    private long leavesTaken;
    private double attendancePercentage;

    public DashboardStatsResponse(String name, Long employeeId, String email, long totalDays, long daysPresent, long leavesTaken, double attendancePercentage) {
        this.name = name;
        this.employeeId = employeeId;
        this.email = email;
        this.totalDays = totalDays;
        this.daysPresent = daysPresent;
        this.leavesTaken = leavesTaken;
        this.attendancePercentage = attendancePercentage;
    }

    public String getName() { return name; }

    public Long getEmployeeId() { return employeeId; }

    public String getEmail() { return email; }
    public long getTotalDays() { return totalDays; }
    public long getDaysPresent() { return daysPresent; }
    public long getLeavesTaken() { return leavesTaken; }
    public double getAttendancePercentage() { return attendancePercentage; }
}

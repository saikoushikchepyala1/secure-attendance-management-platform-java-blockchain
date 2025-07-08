package com.attendance.response;

public class RequestHistoryResponse {
    private String type;
    private String date;
    private String status;

    public RequestHistoryResponse(String type, String date, String status) {
        this.type = type;
        this.date = date;
        this.status = status;
    }

    public String getType() { return type; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}

package com.attendance.response;

public class LoginResponse {
    private String name;
    private String role;
    private Long id;

    public LoginResponse(Long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public Long getId() { return id; }
}

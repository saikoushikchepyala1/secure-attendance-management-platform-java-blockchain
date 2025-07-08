package com.attendance.controller;

import com.attendance.model.Employee;
import com.attendance.repository.EmployeeRepository;
import com.attendance.request.LoginRequest;
import com.attendance.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginData) {
        Employee employee = employeeRepo.findByEmail(loginData.getEmail());

        if (employee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email");
        }

        String storedPassword = employee.getPassword();
        String inputPassword = loginData.getPassword();

        boolean passwordMatches;

        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            passwordMatches = passwordEncoder.matches(inputPassword, storedPassword);
        }

        else {
            passwordMatches = inputPassword.equals(storedPassword);
        }

        if (passwordMatches) {
            return ResponseEntity.ok(new LoginResponse(employee.getId(), employee.getName(), employee.getRole()));
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }
}

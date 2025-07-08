package com.attendance.controller;

import com.attendance.model.Employee;
import com.attendance.model.InviteToken;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.InviteTokenRepository;
import com.attendance.service.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private InviteTokenRepository inviteTokenRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public String register(@RequestParam String token,
                           @RequestParam String name,
                           @RequestParam String password,
                           @RequestParam String department) {
        Optional<InviteToken> optionalToken = inviteService.validateToken(token);
        if (optionalToken.isEmpty()) {
            return "Invalid or expired invite token!";
        }

        InviteToken validToken = optionalToken.get();

        if (employeeRepository.findOptionalByEmail(validToken.getEmail()).isPresent()) {
            return "Account already registered!";
        }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setEmail(validToken.getEmail());
        employee.setDepartment(department);

        employee.setPassword(passwordEncoder.encode(password));
        employee.setRole("EMPLOYEE");

        employeeRepository.save(employee);
        inviteService.markUsed(validToken);
        return "Registration successful!";
    }
}

package com.attendance.controller;

import com.attendance.model.Employee;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.AttendanceRepository;
import com.attendance.response.DashboardStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")

public class AdminController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @GetMapping("/employees")
    public List<DashboardStatsResponse> getAllEmployeeStats() {
        List<Employee> employees = employeeRepo.findAll();
        List<DashboardStatsResponse> statsList = new ArrayList<>();

        for (Employee emp : employees) {
            long totalDays = attendanceRepo.countByEmployeeId(emp.getId());
            long presentDays = attendanceRepo.countByEmployeeIdAndStatusIn(emp.getId(), Arrays.asList("PRESENT", "WFH"));

            long leaves = attendanceRepo.countByEmployeeIdAndStatus(emp.getId(), "LEAVE");

            double percentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;

            statsList.add(new DashboardStatsResponse(
                    emp.getName(),
                    emp.getId(),
                    emp.getEmail(),
                    totalDays,
                    presentDays,
                    leaves,
                    percentage
            ));
        }

        return statsList;
    }


}

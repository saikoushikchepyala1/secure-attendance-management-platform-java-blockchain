package com.attendance.controller;

import com.attendance.blockchain.BlockchainService;
import com.attendance.model.Attendance;
import com.attendance.model.Employee;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private EmployeeRepository employeeRepo;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Attendance data) {
        Long employeeId = data.getEmployeeId();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        LocalTime cutoff = LocalTime.of(10, 0);

        boolean alreadyMarked = attendanceRepo.existsByEmployeeIdAndDate(employeeId, today);
        if (alreadyMarked) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance already marked for today");
        }

        data.setDate(today);

        String status;
        String message;

        if (now.isAfter(cutoff)) {
            status = "ABSENT";
            message = "You are late. Marked as ABSENT and saved to blockchain.";
        } else {
            status = "PRESENT";
            message = "Attendance marked as PRESENT and saved to blockchain.";
        }

        data.setStatus(status);
        attendanceRepo.save(data);

        try {
            blockchainService.logAttendance(
                    employeeId,
                    today.toString(),
                    mapStatusToEnum(status)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Blockchain logging failed.");
        }

        return ResponseEntity.ok(message);
    }

    private BigInteger mapStatusToEnum(String status) {
        return switch (status.toUpperCase()) {
            case "PRESENT" -> BigInteger.valueOf(1);
            case "ABSENT" -> BigInteger.valueOf(2);
            case "WFH" -> BigInteger.valueOf(3);
            case "LEAVE" -> BigInteger.valueOf(4);
            default -> BigInteger.ZERO;
        };
    }

    @GetMapping("/summary/{empId}")
    public Map<String, Object> getEmployeeSummary(@PathVariable Long empId) {
        long total = attendanceRepo.countByEmployeeId(empId);
        long present = attendanceRepo.countByEmployeeIdAndStatusIn(empId, Arrays.asList("PRESENT", "WFH"));

        long leaves = attendanceRepo.countByEmployeeIdAndStatus(empId, "LEAVE");

        double percent = total > 0 ? (present * 100.0 / total) : 0;

        Employee emp = employeeRepo.findById(empId).orElse(null);

        Map<String, Object> summary = new HashMap<>();
        summary.put("employeeId", empId);
        summary.put("name", emp != null ? emp.getName() : "");
        summary.put("daysPresent", present);
        summary.put("totalDays", total);
        summary.put("leaveCount", leaves);
        summary.put("attendancePercentage", percent);

        return summary;
    }

    @GetMapping("/calendar/{empId}")
    public List<Attendance> getCalendar(@PathVariable Long empId) {

        return attendanceRepo.findByEmployeeId(empId);
    }
}

package com.attendance.controller;

import com.attendance.blockchain.BlockchainService;
import com.attendance.model.Attendance;
import com.attendance.model.Employee;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    private EmployeeRepository employeeRepo;

    @Autowired
    private HolidayRepository holidayRepo;

    @Autowired
    private BlockchainService blockchainService;

    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(@RequestBody Attendance data) {
        Long employeeId = data.getEmployeeId();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        LocalTime cutoff = LocalTime.of(10, 0);

        if (holidayRepo.existsByDate(today)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Today is a holiday. No attendance required.");
        }

        boolean alreadyMarked = attendanceRepo.existsByEmployeeIdAndDate(employeeId, today);
        if (alreadyMarked) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Attendance already marked for today");
        }

        data.setDate(today);

        String status;
        String message;

        if (now.isAfter(cutoff)) {
            status = "ABSENT";
            message = "You are late. Marked as ABSENT.";
        } else {
            status = "PRESENT";
            message = "Attendance marked as PRESENT.";
        }

        data.setStatus(status);
        attendanceRepo.save(data);

        return ResponseEntity.ok(message);
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

    // Scheduled job to run at 7PM IST daily
    @Scheduled(cron = "0 0 19 * * ?", zone = "Asia/Kolkata")
    public void logDailyAttendanceHash() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        if (holidayRepo.existsByDate(today)) {
            return; // Skip logging if it's a holiday
        }

        List<Attendance> allToday = attendanceRepo.findByDate(today);

        if (allToday.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        for (Attendance a : allToday) {
            sb.append(a.getEmployeeId())
                    .append("|")
                    .append(a.getStatus())
                    .append(";");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            String hashHex = String.format("%064x", new BigInteger(1, hashBytes));
            blockchainService.logDailyHash(today.toString(), hashHex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

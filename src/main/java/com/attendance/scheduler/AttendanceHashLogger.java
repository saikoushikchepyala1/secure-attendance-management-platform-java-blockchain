package com.attendance.scheduler;

import com.attendance.blockchain.BlockchainService;
import com.attendance.model.Attendance;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.List;

@Component
public class AttendanceHashLogger {

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private HolidayRepository holidayRepo;

    @Autowired
    private BlockchainService blockchainService;

    @Scheduled(cron = "0 0 19 * * *", zone = "Asia/Kolkata") // 7 PM IST daily
    public void logHashForToday() {
        LocalDate today = LocalDate.now();

        if (holidayRepo.existsByDate(today)) {
            System.out.println(" Today is a holiday. Skipping hash log.");
            return;
        }

        List<Attendance> todayRecords = attendanceRepo.findByDate(today);

        if (todayRecords.isEmpty()) {
            System.out.println(" No attendance today. Skipping hash log.");
            return;
        }

        try {
            StringBuilder builder = new StringBuilder();
            for (Attendance record : todayRecords) {
                builder.append(record.getEmployeeId()).append("-").append(record.getStatus()).append(";");
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(builder.toString().getBytes());
            String hashHex = bytesToHex(hashBytes);

            blockchainService.logDailyHash(today.toString(), hashHex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return "0x" + hex.toString();
    }
}

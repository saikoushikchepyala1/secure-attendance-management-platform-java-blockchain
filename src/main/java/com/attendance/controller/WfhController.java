package com.attendance.controller;

import com.attendance.model.WfhRequest;
import com.attendance.repository.WfhRequestRepository;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.LeaveRequestRepository;
import com.attendance.repository.HolidayRepository;


import com.attendance.model.Attendance;
import com.attendance.request.WfhRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/wfh")
@CrossOrigin(origins = "*")
public class WfhController {

    @Autowired
    private WfhRequestRepository wfhRepo;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private LeaveRequestRepository leaveRepo;

    @Autowired
    private HolidayRepository holidayRepo;


    @PostMapping("/request")
    public ResponseEntity<?> requestWfh(@RequestBody WfhRequestDTO request) {
        LocalDate date = LocalDate.parse(request.getDate());

        LocalDate today = LocalDate.now();

        if (!date.equals(today)) {
            return ResponseEntity.badRequest().body("WFH request must be for today.");
        }

        if (holidayRepo.existsByDate(date)) {
            return ResponseEntity.badRequest().body("Cannot request on a holiday.");
        }

        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        if (now.isAfter(LocalTime.of(10, 0))) {
            return ResponseEntity.badRequest().body("Too late to request WFH. Deadline is 10 AM.");
        }

        // Reject if already marked attendance
        if (attendanceRepo.existsByEmployeeIdAndDate(request.getEmployeeId(), date)) {
            return ResponseEntity.badRequest().body("Attendance already marked for today.");
        }

        if (wfhRepo.existsByEmployeeIdAndDate(request.getEmployeeId(), date)) {
            return ResponseEntity.badRequest().body("Already requested WFH for this date");
        }

        if (leaveRepo.existsByEmployeeIdAndDate(request.getEmployeeId(), date)) {
            return ResponseEntity.badRequest().body("Already requested Leave for this date");
        }

        WfhRequest wfh = new WfhRequest();
        wfh.setEmployeeId(request.getEmployeeId());
        wfh.setDate(date);
        wfh.setStatus("PENDING");

        wfhRepo.save(wfh);
        return ResponseEntity.ok("WFH request submitted");
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approveWFH(@PathVariable Long id) {
        WfhRequest req = wfhRepo.findById(id).orElse(null);
        if (req == null) return ResponseEntity.notFound().build();

        req.setStatus("APPROVED");
        wfhRepo.save(req);

        Attendance att = new Attendance();
        att.setEmployeeId(req.getEmployeeId());
        att.setDate(req.getDate());
        att.setStatus("WFH");
        attendanceRepo.save(att);

        return ResponseEntity.ok("WFH Approved");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectWFH(@PathVariable Long id) {
        WfhRequest req = wfhRepo.findById(id).orElse(null);
        if (req == null) return ResponseEntity.notFound().build();

        req.setStatus("REJECTED");
        wfhRepo.save(req);

        Attendance att = new Attendance();
        att.setEmployeeId(req.getEmployeeId());
        att.setDate(req.getDate());
        att.setStatus("ABSENT");
        attendanceRepo.save(att);

        return ResponseEntity.ok("WFH Rejected and marked as ABSENT");
    }
}

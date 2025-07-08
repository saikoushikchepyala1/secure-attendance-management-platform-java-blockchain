package com.attendance.controller;

import com.attendance.model.LeaveRequest;
import com.attendance.model.Attendance;
import com.attendance.repository.LeaveRequestRepository;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.WfhRequestRepository;
import com.attendance.request.LeaveRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/leave")
@CrossOrigin(origins = "*")
public class LeaveController {

    @Autowired
    private LeaveRequestRepository leaveRepo;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private WfhRequestRepository wfhRepo;


    // ✅ Submit Leave Request
    @PostMapping("/request")
    public ResponseEntity<?> requestLeave(@RequestBody LeaveRequestDTO request) {
        LocalDate date = LocalDate.parse(request.getDate());

        if (leaveRepo.existsByEmployeeIdAndDate(request.getEmployeeId(), date)) {
            return ResponseEntity.badRequest().body("Already requested leave for this date.");
        }

        if (wfhRepo.existsByEmployeeIdAndDate(request.getEmployeeId(), date)) {
            return ResponseEntity.badRequest().body("Already requested WFH for this date.");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployeeId(request.getEmployeeId());
        leave.setDate(date);
        leave.setStatus("PENDING");

        leaveRepo.save(leave);
        return ResponseEntity.ok("Leave request submitted");
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approveLeave(@PathVariable Long id) {
        LeaveRequest req = leaveRepo.findById(id).orElse(null);
        if (req == null) return ResponseEntity.notFound().build();

        req.setStatus("APPROVED");
        leaveRepo.save(req);

        Attendance att = new Attendance();
        att.setEmployeeId(req.getEmployeeId());
        att.setDate(req.getDate());
        att.setStatus("LEAVE");
        attendanceRepo.save(att);

        return ResponseEntity.ok("Leave Approved");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectLeave(@PathVariable Long id) {
        LeaveRequest req = leaveRepo.findById(id).orElse(null);
        if (req == null) return ResponseEntity.notFound().build();

        req.setStatus("REJECTED");
        leaveRepo.save(req);
        Attendance att = new Attendance();
        att.setEmployeeId(req.getEmployeeId());
        att.setDate(req.getDate());
        att.setStatus("ABSENT");
        attendanceRepo.save(att);

        return ResponseEntity.ok("Leave Rejected and marked as ABSENT");
    }

}

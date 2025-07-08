package com.attendance.controller;

import com.attendance.model.WfhRequest;
import com.attendance.repository.WfhRequestRepository;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.LeaveRequestRepository;

import com.attendance.model.Attendance;
import com.attendance.request.WfhRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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


    @PostMapping("/request")
    public ResponseEntity<?> requestWfh(@RequestBody WfhRequestDTO request) {
        LocalDate date = LocalDate.parse(request.getDate());

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

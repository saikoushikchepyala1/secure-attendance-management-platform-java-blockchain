package com.attendance.controller;

import com.attendance.model.Attendance;
import com.attendance.model.LeaveRequest;
import com.attendance.model.WfhRequest;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.LeaveRequestRepository;
import com.attendance.repository.WfhRequestRepository;
import com.attendance.response.RequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/requests")
@CrossOrigin(origins = "*")
public class RequestController {

    @Autowired
    private LeaveRequestRepository leaveRepo;

    @Autowired
    private WfhRequestRepository wfhRepo;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @GetMapping("/pending")
    public List<RequestDTO> getAllPendingRequests() {
        LocalDate today = LocalDate.now();

        List<LeaveRequest> staleLeaves = leaveRepo.findByStatusAndDateBefore("PENDING", today);
        for (LeaveRequest leave : staleLeaves) {
            leave.setStatus("EXPIRED");
            leaveRepo.save(leave);

            Attendance att = new Attendance();
            att.setEmployeeId(leave.getEmployeeId());
            att.setDate(leave.getDate());
            att.setStatus("ABSENT");
            attendanceRepo.save(att);
        }


        List<WfhRequest> staleWfhs = wfhRepo.findByStatusAndDateBefore("PENDING", today);
        for (WfhRequest wfh : staleWfhs) {
            wfh.setStatus("EXPIRED");
            wfhRepo.save(wfh);

            Attendance att = new Attendance();
            att.setEmployeeId(wfh.getEmployeeId());
            att.setDate(wfh.getDate());
            att.setStatus("ABSENT");
            attendanceRepo.save(att);
        }

        List<LeaveRequest> leave = leaveRepo.findByStatus("PENDING");
        List<WfhRequest> wfh = wfhRepo.findByStatus("PENDING");

        List<RequestDTO> requests = new ArrayList<>();

        leave.forEach(lr -> requests.add(
                new RequestDTO(lr.getId(), lr.getEmployeeId(), lr.getDate(), "LEAVE", lr.getStatus()))
        );
        wfh.forEach(wr -> requests.add(
                new RequestDTO(wr.getId(), wr.getEmployeeId(), wr.getDate(), "WFH", wr.getStatus()))
        );


        requests.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));


        System.out.println("Total requests returned: " + requests.size());

        return requests;
    }
}

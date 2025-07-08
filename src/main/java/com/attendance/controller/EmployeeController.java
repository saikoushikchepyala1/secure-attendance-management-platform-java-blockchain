package com.attendance.controller;

import com.attendance.model.LeaveRequest;
import com.attendance.model.WfhRequest;
import com.attendance.repository.LeaveRequestRepository;
import com.attendance.repository.WfhRequestRepository;
import com.attendance.response.RequestHistoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private LeaveRequestRepository leaveRepo;

    @Autowired
    private WfhRequestRepository wfhRepo;

    @GetMapping("/requests/{empId}")
    public List<RequestHistoryResponse> getEmployeeRequestHistory(@PathVariable Long empId) {
        List<RequestHistoryResponse> history = new ArrayList<>();

        for (LeaveRequest leave : leaveRepo.findByEmployeeId(empId)) {
            history.add(new RequestHistoryResponse("LEAVE", leave.getDate().toString(), leave.getStatus()));
        }

        for (WfhRequest wfh : wfhRepo.findByEmployeeId(empId)) {
            history.add(new RequestHistoryResponse("WFH", wfh.getDate().toString(), wfh.getStatus()));
        }

        return history;
    }
}

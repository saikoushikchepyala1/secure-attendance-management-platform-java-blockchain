package com.attendance.repository;

import com.attendance.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    List<LeaveRequest> findByStatus(String status);
    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<LeaveRequest> findByStatusAndDateBefore(String status, LocalDate date);

}

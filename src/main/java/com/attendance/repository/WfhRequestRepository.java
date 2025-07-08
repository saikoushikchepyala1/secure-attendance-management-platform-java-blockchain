package com.attendance.repository;

import com.attendance.model.WfhRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WfhRequestRepository extends JpaRepository<WfhRequest, Long> {
    List<WfhRequest> findByEmployeeId(Long employeeId);
    List<WfhRequest> findByStatus(String status);
    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<WfhRequest> findByStatusAndDateBefore(String status, LocalDate date);

}

package com.attendance.controller;

import com.attendance.model.Holiday;
import com.attendance.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/holidays")
@CrossOrigin(origins = "*")
public class HolidayController {

    @Autowired
    private HolidayRepository repo;

    @GetMapping
    public List<Holiday> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Holiday add(@RequestBody Holiday holiday) {
        return repo.save(holiday);
    }
}

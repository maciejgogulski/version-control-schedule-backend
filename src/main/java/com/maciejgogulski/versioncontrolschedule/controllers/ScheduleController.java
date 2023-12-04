package com.maciejgogulski.versioncontrolschedule.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import com.maciejgogulski.versioncontrolschedule.dto.ScheduleDto;
import com.maciejgogulski.versioncontrolschedule.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public ResponseEntity<String> addSchedule(@RequestBody ScheduleDto scheduleDto) {
        Schedule schedule = scheduleService.addSchedule(scheduleDto.name());

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(schedule);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // TODO Handle unique name constraint violation exception.
    }

    @GetMapping("/{scheduleId}")
    ResponseEntity<String> getSchedule(@PathVariable Long scheduleId) {
        try {
            Schedule schedule = scheduleService.getSchedule(scheduleId);
            String responseBody = objectMapper.writeValueAsString(schedule);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    ResponseEntity<String> getSchedules() {
        try {
            List<Schedule> schedules = scheduleService.getSchedules();
            String responseBody = objectMapper.writeValueAsString(schedules);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: Mapping exceptions to http response codes
        }
    }

    @PutMapping()
    ResponseEntity<String> updateSchedule(@RequestBody ScheduleDto scheduleDto) {
        try {
            ScheduleDto updatedSchedule = scheduleService.updateSchedule(scheduleDto);

            String responseBody = objectMapper.writeValueAsString(updatedSchedule);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // TODO Handle unique name constraint violation exception.
    }

    @DeleteMapping("/{scheduleId}")
    ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {
        try {
            scheduleService.deleteSchedule(scheduleId);

            return new ResponseEntity<>("""
                    {
                        status: "Schedule deleted."
                    }
                    """, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}

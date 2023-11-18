package com.maciejgogulski.eventschedulingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.dto.StagedEventDto;
import com.maciejgogulski.eventschedulingbackend.service.impl.StagedEventServiceImpl;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staged-event")
public class StagedEventController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StagedEventServiceImpl stagedEventService;

    public StagedEventController(StagedEventServiceImpl stagedEventService) {
        this.stagedEventService = stagedEventService;
    }

    @PostMapping
    public ResponseEntity<String> addStagedEvent(@RequestBody StagedEventDto stagedEventDto) {
        stagedEventDto = stagedEventService.create(stagedEventDto);

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(stagedEventDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{stagedEventId}/modification")
    public ResponseEntity<?> getModificationsForStagedEvent(@PathVariable Long stagedEventId) {
        List<ModificationDto> modificationDtoList = stagedEventService.getModificationsForStagedEvent(stagedEventId);
        return new ResponseEntity<>(modificationDtoList, HttpStatus.OK);
    }

    @GetMapping("/schedule-tag/{scheduleTagId}/latest")
    public ResponseEntity<?> getLatestStagedEventForSchedule(@PathVariable Long scheduleTagId) {
        StagedEventDto stagedEventDto = stagedEventService.getLatestStagedEventForSchedule(scheduleTagId);
        return new ResponseEntity<>(stagedEventDto, HttpStatus.OK);
    }

    @PutMapping("/{stagedEventId}/commit")
    public ResponseEntity<?> commitStagedEvent(@PathVariable Long stagedEventId) throws MessagingException {
        stagedEventService.commitStagedEvent(stagedEventId);
        return new ResponseEntity<>("""
                {
                    "status": "Commited staged event with id: %s"
                }
                """.formatted(stagedEventId), HttpStatus.OK);
    }
}

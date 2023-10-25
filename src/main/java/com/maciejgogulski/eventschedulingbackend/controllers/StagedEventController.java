package com.maciejgogulski.eventschedulingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.dto.StagedEventDto;
import com.maciejgogulski.eventschedulingbackend.service.impl.StagedEventServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staged-event")
public class StagedEventController {

    private final Logger logger = LoggerFactory.getLogger(StagedEventController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StagedEventServiceImpl stagedEventService;

    public StagedEventController(StagedEventServiceImpl stagedEventService) {
        this.stagedEventService = stagedEventService;
    }

    @PostMapping
    public ResponseEntity<String> addStagedEvent(@RequestBody StagedEventDto stagedEventDto) {
        logger.info("[addStagedEvent] Creating staged event for schedule tag with id: " + stagedEventDto.scheduleTagId());
        stagedEventDto = stagedEventService.create(stagedEventDto);
        logger.info("[addStagedEvent] Successfully created staged event with id: " + stagedEventDto.id());

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(stagedEventDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{stagedEventId}/modification")
    public ResponseEntity<String> getModificationsForStagedEvent(@PathVariable Long stagedEventId) throws JsonProcessingException {
        List<ModificationDto> modificationDtoList = stagedEventService.getModificationsForStagedEvent(stagedEventId);
        String jsonModifications = objectMapper.writeValueAsString(modificationDtoList);
        return new ResponseEntity<>(jsonModifications, HttpStatus.OK);
    }
}

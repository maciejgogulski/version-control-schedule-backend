package com.maciejgogulski.eventschedulingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.dto.ScheduleTagDto;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleTagService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule-tag")
public class ScheduleTagController {


    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ScheduleTagService scheduleTagService;

    public ScheduleTagController(ScheduleTagService scheduleTagService) {
        this.scheduleTagService = scheduleTagService;
    }


    /**
     * Create a new schedule tag.
     * @param scheduleTagDto New schedule tag data.
     * @return Created schedule tag.
     */
    @PostMapping
    public ResponseEntity<String> addScheduleTag(@RequestBody ScheduleTagDto scheduleTagDto) {
        ScheduleTag scheduleTag = scheduleTagService.addScheduleTag(scheduleTagDto.name());

        String responseBody = null;
        try {
            responseBody = objectMapper.writeValueAsString(scheduleTag);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // TODO Handle unique name constraint violation exception.
    }

    /**
     * Get existing schedule tag.
     * @param scheduleTagId Unique id of a schedule tag.
     * @return Schedule tag.
     */
    @GetMapping("/{scheduleTagId}")
    ResponseEntity<String> getScheduleTag(@PathVariable Long scheduleTagId) {
        try {
            ScheduleTag scheduleTag = scheduleTagService.getScheduleTag(scheduleTagId);
            String responseBody = objectMapper.writeValueAsString(scheduleTag);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    ResponseEntity<String> getScheduleTags() {
        try {
            List<ScheduleTag> scheduleTags = scheduleTagService.getScheduleTags();
            String responseBody = objectMapper.writeValueAsString(scheduleTags);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: Mapping exceptions to http response codes
        }
    }

    /**
     * Updates existing schedule tag.
     * @param scheduleTagDto Updated schedule tag data.
     * @return Updated schedule tag.
     */
    @PutMapping()
    ResponseEntity<String> updateScheduleTag(@RequestBody ScheduleTagDto scheduleTagDto) {
        try {
            ScheduleTagDto updatedScheduleTag = scheduleTagService.updateScheduleTag(scheduleTagDto);

            String responseBody = objectMapper.writeValueAsString(updatedScheduleTag);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // TODO Handle unique name constraint violation exception.
    }


    /**
     * Delete existing schedule tag.
     * @param scheduleTagId Schedule tag id.
     * @return Response message.
     */
    @DeleteMapping("/{scheduleTagId}")
    ResponseEntity<String> deleteScheduleTag(@PathVariable Long scheduleTagId) {
        try {
            scheduleTagService.deleteScheduleTag(scheduleTagId);

            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag deleted."
                    }
                    """, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}

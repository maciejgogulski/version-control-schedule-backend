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

    private final Logger logger = LoggerFactory.getLogger(ScheduleTagController.class);

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
        logger.info("[addScheduleTag] Creating schedule tag with name: " + scheduleTagDto.name());
        ScheduleTag scheduleTag = scheduleTagService.addScheduleTag(scheduleTagDto.name());
        logger.info("[addScheduleTag] Successfully created schedule tag with name: " + scheduleTagDto.name());

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
            logger.info("[getScheduleTag] Getting schedule tag with id: " + scheduleTagId);
            ScheduleTag scheduleTag = scheduleTagService.getScheduleTag(scheduleTagId);
            logger.info("[getScheduleTag] Successfully fetched schedule tag with id: " + scheduleTagId);
            String responseBody = objectMapper.writeValueAsString(scheduleTag);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[getScheduleTag] Schedule tag not found");
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
            logger.info("[getScheduleTags] Getting all schedule tags");
            List<ScheduleTag> scheduleTags = scheduleTagService.getScheduleTags();
            logger.info("[getScheduleTags] Successfully fetched " + scheduleTags.size() + " schedule tags") ;
            String responseBody = objectMapper.writeValueAsString(scheduleTags);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[getScheduleTag] Schedule tag not found");
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
            logger.info("[updateScheduleTag] Updating schedule tag with id: " + scheduleTagDto.id());
            ScheduleTagDto updatedScheduleTag = scheduleTagService.updateScheduleTag(scheduleTagDto);
            logger.info("[updateScheduleTag] Successfully updated schedule tag with id: " + scheduleTagDto.id());

            String responseBody = objectMapper.writeValueAsString(updatedScheduleTag);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[updateScheduleTag] Schedule tag not found");
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
            logger.info("[deleteScheduleTag] Deleting schedule tag with id: " + scheduleTagId);
            scheduleTagService.deleteScheduleTag(scheduleTagId);
            logger.info("[deleteScheduleTag] Successfully deleted schedule tag with id: " + scheduleTagId);

            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag deleted."
                    }
                    """, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[deleteScheduleTag] Schedule tag not found");
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}

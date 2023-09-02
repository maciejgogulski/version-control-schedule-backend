package com.maciejgogulski.eventschedulingbackend.controllers;

import com.google.gson.Gson;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleTagService;
import com.maciejgogulski.eventschedulingbackend.util.GsonWrapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule-tag")
public class ScheduleTagController {

    private final Logger logger = LoggerFactory.getLogger(ScheduleTagController.class);

    private final Gson gson = GsonWrapper.getInstance();

    private final ScheduleTagService scheduleTagService;

    public ScheduleTagController(ScheduleTagService scheduleTagService) {
        this.scheduleTagService = scheduleTagService;
    }


    /**
     * Create a new schedule tag.
     * @param name Schedule tag name.
     * @return Created schedule tag.
     */
    @PostMapping
    public ResponseEntity<String> addScheduleTag(@RequestBody String name) {
        logger.info("[addScheduleTag] Creating schedule tag with name: " + name);
        ScheduleTag scheduleTag = scheduleTagService.addScheduleTag(name);
        logger.info("[addScheduleTag] Successfully created schedule tag with name: " + name);

        String responseBody = gson.toJson(scheduleTag);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);

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
            String responseBody = gson.toJson(scheduleTag);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[getScheduleTag] Schedule tag not found");
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates existing schedule tag.
     * @param scheduleTag Updated schedule tag data.
     * @return Updated schedule tag.
     */
    @PutMapping()
    ResponseEntity<String> updateScheduleTag(@RequestBody ScheduleTag scheduleTag) {
        try {
            // TODO: Change ScheduleTag to DTO
            logger.info("[updateScheduleTag] Updating schedule tag with id: " + scheduleTag.getId());
            ScheduleTag updatedScheduleTag = scheduleTagService.updateScheduleTag(scheduleTag);
            logger.info("[updateScheduleTag] Successfully updated schedule tag with id: " + scheduleTag.getId());

            String responseBody = gson.toJson(updatedScheduleTag);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[updateScheduleTag] Schedule tag not found");
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
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

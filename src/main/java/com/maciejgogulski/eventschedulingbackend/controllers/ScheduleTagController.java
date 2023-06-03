package com.maciejgogulski.eventschedulingbackend.controllers;

import com.google.gson.Gson;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleTagService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule-tag")
public class ScheduleTagController {

    private final Gson gson = new Gson();

    @Autowired
    private ScheduleTagService scheduleTagService;


    /**
     * Create a new schedule tag.
     * @param name Schedule tag name.
     * @return Created schedule tag.
     */
    @PostMapping
    public ResponseEntity<String> addScheduleTag(@RequestBody String name) {
        ScheduleTag scheduleTag = scheduleTagService.addScheduleTag(name);
        String responseBody = gson.toJson(scheduleTag);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);

        // TODO Handle unique name constraint violation exception.
    }

    /**
     * Get schedule tag.
     * @param scheduleTagId Unique id of a schedule tag.
     * @return Schedule tag.
     */
    @GetMapping("/{scheduleTagId}")
    ResponseEntity<String> getScheduleTag(@PathVariable Long scheduleTagId) {
        try {
            ScheduleTag scheduleTag = scheduleTagService.getScheduleTag(scheduleTagId);
            String responseBody = gson.toJson(scheduleTag);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}

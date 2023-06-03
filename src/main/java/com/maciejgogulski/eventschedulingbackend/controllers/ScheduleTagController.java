package com.maciejgogulski.eventschedulingbackend.controllers;

import com.google.gson.Gson;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

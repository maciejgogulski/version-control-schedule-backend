package com.maciejgogulski.eventschedulingbackend.controllers;

import com.google.gson.Gson;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleBlockService;
import com.maciejgogulski.eventschedulingbackend.util.GsonWrapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule-block")
public class ScheduleBlockController {

    private final Gson gson = GsonWrapper.getInstance();

    @Autowired
    private ScheduleBlockService scheduleBlockService;


    /**
     * Create a new schedule block.
     * @param scheduleBlock Schedule block json.
     * @return Created schedule block.
     */
    @PostMapping
    public ResponseEntity<String> addScheduleBlock(@RequestBody ScheduleBlock scheduleBlock) {
        scheduleBlock = scheduleBlockService.addScheduleBlock(scheduleBlock);
        String responseBody = gson.toJson(scheduleBlock);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);

        // TODO Handle wrong schedule tag id exception.
    }

    /**
     * Get existing schedule block.
     * @param scheduleBlockId Unique id of a schedule block.
     * @return Schedule block.
     */
    @GetMapping("/{scheduleBlockId}")
    ResponseEntity<String> getScheduleBlock(@PathVariable Long scheduleBlockId) {
        try {
            ScheduleBlock scheduleBlock = scheduleBlockService.getScheduleBlock(scheduleBlockId);
            String responseBody = gson.toJson(scheduleBlock);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates existing schedule block.
     * @param scheduleBlock Updated schedule block data.
     * @return Updated schedule block.
     */
    @PutMapping()
    ResponseEntity<String> updateScheduleBlock(@RequestBody ScheduleBlock scheduleBlock) {
        try {
            ScheduleBlock updatedScheduleBlock = scheduleBlockService.updateScheduleBlock(scheduleBlock);
            String responseBody = gson.toJson(updatedScheduleBlock);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }


    /**
     * Delete existing schedule block.
     * @param scheduleBlockId Schedule block id.
     * @return Response message.
     */
    @DeleteMapping("/{scheduleBlockId}")
    ResponseEntity<String> deleteScheduleTag(@PathVariable Long scheduleBlockId) {
        try {
            scheduleBlockService.deleteScheduleBlock(scheduleBlockId);
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block deleted."
                    }
                    """, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}

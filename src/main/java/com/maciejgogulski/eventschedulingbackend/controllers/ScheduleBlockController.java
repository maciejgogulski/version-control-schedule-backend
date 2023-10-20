package com.maciejgogulski.eventschedulingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.eventschedulingbackend.dto.ParameterDto;
import com.maciejgogulski.eventschedulingbackend.dto.ScheduleBlockDto;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleBlockService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/schedule-block")
public class ScheduleBlockController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ScheduleBlockService scheduleBlockService;

    public ScheduleBlockController(ScheduleBlockService scheduleBlockService) {
        this.scheduleBlockService = scheduleBlockService;
    }

    /**
     * Get schedule blocks of given schedule tag in provided day.
     *
     * @param scheduleTagId ID of a schedule.
     * @param day           Provided day.
     * @return List of schedule blocks.
     */
    @GetMapping("/by-day")
    public ResponseEntity<String> getScheduleBlocksForScheduleByDay(@RequestParam Long scheduleTagId, @RequestParam String day) {
        List<ScheduleBlockDto> blockDtoList = scheduleBlockService.getScheduleBlocksForScheduleByDay(scheduleTagId, day);
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(blockDtoList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("""
                    {
                        "status": "Error parsing response to JSON."
                    }
                    """, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new schedule block.
     *
     * @param scheduleBlockDto Schedule block json.
     * @return Created schedule block.
     */
    @PostMapping
    public ResponseEntity<String> addScheduleBlock(@RequestBody ScheduleBlockDto scheduleBlockDto) {
        try {
            ScheduleBlockDto createdBlockDto = scheduleBlockService.addScheduleBlock(scheduleBlockDto);
            String responseBody;

            responseBody = objectMapper.writeValueAsString(createdBlockDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException | ParseException e) {
            return new ResponseEntity<>("""
                    {
                        "status": "Error parsing response to JSON."
                    }
                    """, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get existing schedule block.
     *
     * @param scheduleBlockId Unique id of a schedule block.
     * @return Schedule block.
     */
    @GetMapping("/{scheduleBlockId}")
    ResponseEntity<String> getScheduleBlock(@PathVariable Long scheduleBlockId) {
        try {
            ScheduleBlockDto blockDto = scheduleBlockService.getScheduleBlock(scheduleBlockId);
            String responseBody = objectMapper.writeValueAsString(blockDto);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("""
                    {
                        "status": "Error parsing response to JSON."
                    }
                    """, HttpStatus.INTERNAL_SERVER_ERROR);
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
     *
     * @param scheduleBlockDto Updated schedule block data.
     * @return Updated schedule block.
     */
    @PutMapping()
    ResponseEntity<String> updateScheduleBlock(@RequestBody ScheduleBlockDto scheduleBlockDto) {
        try {
            ScheduleBlockDto updatedScheduleBlock = scheduleBlockService.updateScheduleBlock(scheduleBlockDto);
            String responseBody = objectMapper.writeValueAsString(updatedScheduleBlock);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException | ParseException e) {
            return new ResponseEntity<>("""
                    {
                        "status": "Error parsing response to JSON."
                    }
                    """, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Delete existing schedule block.
     *
     * @param scheduleBlockId Schedule block id.
     * @return Response message.
     */
    @DeleteMapping("/{scheduleBlockId}")
    ResponseEntity<String> deleteScheduleBlock(@PathVariable Long scheduleBlockId) {
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

    @PutMapping("/parameter")
    ResponseEntity<String> assignParameterToScheduleBlock(@RequestBody ParameterDto parameterDto) {
        try {
            scheduleBlockService.assignParameterToBlock(
                    parameterDto.parameterName(),
                    parameterDto.value(),
                    parameterDto.scheduleBlockId()
            );
            return new ResponseEntity<>("""
                    {
                         status: "Assigned parameter name: %s to schedule block id: %s"
                    }
                     """.formatted(parameterDto.parameterName(), parameterDto.scheduleBlockId()), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{scheduleBlockId}/parameter")
    ResponseEntity<?> getParametersForScheduleBlock(@PathVariable Long scheduleBlockId) {
        try {
            List<ParameterDto> parameterDtoList = scheduleBlockService.getParametersForBlock(scheduleBlockId);
            return new ResponseEntity<>(parameterDtoList, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}



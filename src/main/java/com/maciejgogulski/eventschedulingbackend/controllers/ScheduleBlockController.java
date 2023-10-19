package com.maciejgogulski.eventschedulingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.eventschedulingbackend.dto.BlockParameterDto;
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

    private final Logger logger = LoggerFactory.getLogger(ScheduleBlockController.class);

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
        logger.info("[getScheduleBlocksForScheduleByDay] Getting schedule blocks for tag id: " + scheduleTagId + " and day: " + day);
        List<ScheduleBlockDto> blockDtoList = scheduleBlockService.getScheduleBlocksForScheduleByDay(scheduleTagId, day);
        logger.info("[getScheduleBlocksForScheduleByDay] Successfully fetched " + blockDtoList.size() + " blocks");
        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(blockDtoList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            logger.error("[getScheduleBlocksForScheduleByDay][ERROR] Error parsing response to JSON");
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
            logger.info("[addScheduleBlock] Adding new block: " + scheduleBlockDto.name());
            ScheduleBlockDto createdBlockDto = scheduleBlockService.addScheduleBlock(scheduleBlockDto);
            logger.info("[addScheduleBlock] Successfully created new block with id: " + createdBlockDto.id());
            String responseBody;

            responseBody = objectMapper.writeValueAsString(createdBlockDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException | ParseException e) {
            logger.error("[addScheduleBlock][ERROR] Error parsing response to JSON");
            return new ResponseEntity<>("""
                    {
                        "status": "Error parsing response to JSON."
                    }
                    """, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            logger.error("[addScheduleBlock][ERROR] Schedule tag not found");
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
            logger.info("[getScheduleBlock] Getting block with id: " + scheduleBlockId);
            ScheduleBlockDto blockDto = scheduleBlockService.getScheduleBlock(scheduleBlockId);
            logger.info("[getScheduleBlock] Successfully fetched block with id: " + scheduleBlockId);
            String responseBody = objectMapper.writeValueAsString(blockDto);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            logger.error("[getScheduleBlock][ERROR] Error parsing response to JSON");
            return new ResponseEntity<>("""
                    {
                        "status": "Error parsing response to JSON."
                    }
                    """, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            logger.error("[getScheduleBlock][ERROR] Schedule block not found");
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
            logger.info("[updateScheduleBlock] Updating block with id: " + scheduleBlockDto.id());
            ScheduleBlockDto updatedScheduleBlock = scheduleBlockService.updateScheduleBlock(scheduleBlockDto);
            logger.info("[updateScheduleBlock] Successfully updated block with id: " + scheduleBlockDto.id());

            String responseBody = objectMapper.writeValueAsString(updatedScheduleBlock);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[updateScheduleBlock][ERROR] Schedule block not found");
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException | ParseException e) {
            logger.error("[updateScheduleBlock][ERROR] Error parsing response to JSON");
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
            logger.info("[deleteScheduleBlock] Deleting block with id: " + scheduleBlockId);
            scheduleBlockService.deleteScheduleBlock(scheduleBlockId);
            logger.info("[deleteScheduleBlock] Successfully deleted block with id: " + scheduleBlockId);

            return new ResponseEntity<>("""
                    {
                        status: "Schedule block deleted."
                    }
                    """, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[deleteScheduleBlock][ERROR] Schedule block not found");
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/parameter")
    ResponseEntity<String> assignParameterToScheduleBlock(@RequestBody BlockParameterDto blockParameterDto) {
        final String METHOD_TAG = "[assignParameterToScheduleBlock] ";
        try {
            logger.info(METHOD_TAG + "Assigning parameter: " + blockParameterDto.parameterName()
                    + " to schedule block id: " + blockParameterDto.scheduleBlockId());

            scheduleBlockService.assignParameterToBlock(
                    blockParameterDto.parameterName(),
                    blockParameterDto.value(),
                    blockParameterDto.scheduleBlockId()
            );

            logger.info(METHOD_TAG + "Successfully assigned parameter: " + blockParameterDto.parameterName()
                    + " to schedule block id: " + blockParameterDto.scheduleBlockId());

            return new ResponseEntity<>("""
                    {
                         status: "Assigned parameter name: %s to schedule block id: %s"
                    }
                     """.formatted(blockParameterDto.parameterName(), blockParameterDto.scheduleBlockId()), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Schedule block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}



package com.maciejgogulski.versioncontrolschedule.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.versioncontrolschedule.dto.ParameterDto;
import com.maciejgogulski.versioncontrolschedule.dto.BlockDto;
import com.maciejgogulski.versioncontrolschedule.service.BlockService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/block")
public class BlockController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping("/by-day")
    public ResponseEntity<String> getBlocksForScheduleByDay(@RequestParam Long scheduleId, @RequestParam String day) {
        List<BlockDto> blockDtoList = blockService.getBlocksForScheduleByDay(scheduleId, day);
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

    @PostMapping
    public ResponseEntity<String> addBlock(@RequestBody BlockDto blockDto) {
        try {
            BlockDto createdBlockDto = blockService.addBlock(blockDto);
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
                        status: "Schedule not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{blockId}")
    ResponseEntity<String> getBlock(@PathVariable Long blockId) {
        try {
            BlockDto blockDto = blockService.getBlock(blockId);
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
                        status: "Block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping()
    ResponseEntity<String> updateBlock(@RequestBody BlockDto blockDto) {
        try {
            BlockDto updatedBlock = blockService.updateBlock(blockDto);
            String responseBody = objectMapper.writeValueAsString(updatedBlock);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Block not found."
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


    @DeleteMapping("/{blockId}")
    ResponseEntity<String> deleteBlock(@PathVariable Long blockId) {
        try {
            blockService.deleteBlock(blockId);
            return new ResponseEntity<>("""
                    {
                        status: "Block deleted."
                    }
                    """, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/parameter")
    ResponseEntity<String> assignParameterToBlock(@RequestBody ParameterDto parameterDto) {
        try {
            if (parameterDto.id() == null) {
                blockService.assignParameterToBlock(parameterDto);
            } else {
                blockService.updateParameterWithinBlock(parameterDto);
            }

            return new ResponseEntity<>("""
                    {
                         status: "Assigned parameter name: %s to block id: %s"
                    }
                     """.formatted(parameterDto.parameterName(), parameterDto.blockId()), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>("""
                    {
                        status: "Entity not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{blockId}/parameter")
    ResponseEntity<?> getParametersForBlock(@PathVariable Long blockId) {
        try {
            List<ParameterDto> parameterDtoList = blockService.getParametersForBlock(blockId);
            return new ResponseEntity<>(parameterDtoList, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Block not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/parameter/{blockParameterId}")
    ResponseEntity<?> deleteParameterFromBlock(@PathVariable Long blockParameterId) {
        try {
            blockService.deleteParameterFromBlock(blockParameterId);
            return new ResponseEntity<>("""
                    {
                        status: "Successfully deleted block parameter with id: %s"
                    }
                    """.formatted(blockParameterId), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Block parameter not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }
}



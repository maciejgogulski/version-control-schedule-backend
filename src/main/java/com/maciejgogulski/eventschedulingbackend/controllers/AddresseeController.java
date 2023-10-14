package com.maciejgogulski.eventschedulingbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.eventschedulingbackend.dto.AddresseeDto;
import com.maciejgogulski.eventschedulingbackend.service.impl.AddresseeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.AlreadyBoundException;
import java.util.List;

@RestController
@RequestMapping("/addressee")
public class AddresseeController {

    private final Logger logger = LoggerFactory.getLogger(AddresseeController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AddresseeServiceImpl addresseeService;

    public AddresseeController(AddresseeServiceImpl addresseeService) {
        this.addresseeService = addresseeService;
    }

    @PostMapping
    public ResponseEntity<String> addAddressee(@RequestBody AddresseeDto addresseeDto) {
        logger.info("[addAddressee] Creating addressee with name: " + addresseeDto.firstName() + " " + addresseeDto.lastName());
        addresseeDto = addresseeService.create(addresseeDto);
        logger.info("[addAddressee] Successfully created addressee with id: " + addresseeDto.id());

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(addresseeDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<String> getAddressee(@PathVariable Long id) {
        try {
            logger.info("[getAddressee] Getting addressee with id: " + id);
            AddresseeDto addresseeDto = addresseeService.get(id);
            logger.info("[getAddressee] Successfully fetched addressee with id: " + id);
            String responseBody = objectMapper.writeValueAsString(addresseeDto);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[getAddressee] Addressee not found");
            return new ResponseEntity<>("""
                    {
                        status: "Addressee not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    ResponseEntity<String> getAddressees() {
        try {
            logger.info("[getAddressees] Getting all addressees");
            List<AddresseeDto> addresseeDtoList = addresseeService.getAll();
            logger.info("[getAddressees] Successfully fetched " + addresseeDtoList.size() + " addressees");
            String responseBody = objectMapper.writeValueAsString(addresseeDtoList);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[getAddressees] Addressees not found");
            return new ResponseEntity<>("""
                    {
                        status: "Addressees not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: Mapping exceptions to http response codes
        }
    }

    @PutMapping()
    ResponseEntity<String> updateAddressee(@RequestBody AddresseeDto addresseeDto) {
        try {
            logger.info("[updateAddressee] Updating addressee with id: " + addresseeDto.id());
            addresseeDto = addresseeService.update(addresseeDto.id(), addresseeDto);
            logger.info("[updateAddressee] Successfully updated addressee with id: " + addresseeDto.id());

            String responseBody = objectMapper.writeValueAsString(addresseeDto);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[updateAddressee] Addressee not found");
            return new ResponseEntity<>("""
                    {
                        status: "Addressee not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteAddressee(@PathVariable Long id) {
        try {
            logger.info("[deleteAddressee] Deleting addressee with id: " + id);
            addresseeService.delete(id);
            logger.info("[deleteAddressee] Successfully deleted addressee with id: " + id);

            return new ResponseEntity<>("""
                    {
                        status: "Addressee deleted."
                    }
                    """, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[deleteAddressee] Addressee not found");
            return new ResponseEntity<>("""
                    {
                        status: "Addressee not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/schedule-tag/{id}")
    ResponseEntity<String> getAddresseesByScheduleTagId(@PathVariable Long id) {
        final String METHOD_TAG = "[getAddresseeByScheduleTagId] ";
        try {
            logger.info(METHOD_TAG + "Getting addressees for schedule tag id: " + id);
            List<AddresseeDto> addresseeDtoList = addresseeService.getAddressesByScheduleTagId(id);
            logger.info(METHOD_TAG + "Successfully fetched " + addresseeDtoList.size() + " addressees");
            String responseBody = objectMapper.writeValueAsString(addresseeDtoList);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{addresseeId}/schedule-tag/{scheduleTagId}")
    ResponseEntity<String> assignAddresseeToScheduleTag(@PathVariable Long addresseeId, @PathVariable Long scheduleTagId) {
        final String METHOD_TAG = "[assignAddresseeToScheduleTag] ";
        try {
            logger.info(METHOD_TAG + "Assigning addressee id: " + addresseeId + " to schedule tag id: " + scheduleTagId);
            addresseeService.assignAddresseeToScheduleTagId(addresseeId, scheduleTagId);
            logger.info(METHOD_TAG + "Successfully assigned addressee id: " + addresseeId + " to schedule tag id: " + scheduleTagId);
            return new ResponseEntity<>("""
                   {
                        status: "Assigned addressee id: %s to schedule tag id: %s"
                   }
                    """.formatted(addresseeId, scheduleTagId), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
           return new ResponseEntity<>("""
                    {
                        status: "Addressee or schedule tag not found."
                    }
                    """, HttpStatus.NOT_FOUND);
        } catch (AlreadyBoundException e) {
            return new ResponseEntity<>("""
                    {
                        status: "Addressee already assigned to schedule tag."
                    }
                    """, HttpStatus.NOT_ACCEPTABLE);
        }
    }

}

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
            logger.info("[getAddressees] Getting all schedule tags");
            List<AddresseeDto> addresseeDtoList = addresseeService.getAll();
            logger.info("[getAddressees] Successfully fetched " + addresseeDtoList.size() + " addressees") ;
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
}

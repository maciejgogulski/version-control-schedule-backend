package com.maciejgogulski.versioncontrolschedule.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.dto.VersionDto;
import com.maciejgogulski.versioncontrolschedule.service.impl.VersionServiceImpl;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/version")
public class VersionController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final VersionServiceImpl versionServiceImpl;

    public VersionController(VersionServiceImpl versionServiceImpl) {
        this.versionServiceImpl = versionServiceImpl;
    }

    @PostMapping
    public ResponseEntity<String> addVersion(@RequestBody VersionDto versionDto) {
        versionDto = versionServiceImpl.create(versionDto);

        String responseBody;
        try {
            responseBody = objectMapper.writeValueAsString(versionDto);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{versionId}/modification")
    public ResponseEntity<?> getModificationsForVersion(@PathVariable Long versionId) {
        List<ModificationDto> modificationDtoList = versionServiceImpl.getModificationsForVersion(versionId);
        return new ResponseEntity<>(modificationDtoList, HttpStatus.OK);
    }

    @GetMapping("/schedule/{scheduleId}/latest")
    public ResponseEntity<?> getLatestVersionForSchedule(@PathVariable Long scheduleId) {
        VersionDto versionDto = versionServiceImpl.getLatestVersionForSchedule(scheduleId);
        return new ResponseEntity<>(versionDto, HttpStatus.OK);
    }

    @PutMapping("/{versionId}/commit")
    public ResponseEntity<?> commitVersion(@PathVariable Long versionId) throws MessagingException {
        versionServiceImpl.commitVersion(versionId);
        return new ResponseEntity<>("""
                {
                    "status": "Committed version with id: %s"
                }
                """.formatted(versionId), HttpStatus.OK);
    }
}

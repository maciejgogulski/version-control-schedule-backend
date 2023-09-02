package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.controllers.ScheduleTagController;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.dto.ScheduleBlockDto;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleBlockRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleBlockService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleBlockServiceImpl implements ScheduleBlockService {

    private final Logger logger = LoggerFactory.getLogger(ScheduleBlockServiceImpl.class);

    private final ScheduleBlockRepository scheduleBlockRepository;

    private final ScheduleTagRepository scheduleTagRepository;

    public ScheduleBlockServiceImpl(ScheduleBlockRepository scheduleBlockRepository, ScheduleTagRepository scheduleTagRepository) {
        this.scheduleBlockRepository = scheduleBlockRepository;
        this.scheduleTagRepository = scheduleTagRepository;
    }

    @Override
    public ScheduleBlockDto addScheduleBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException {
        logger.debug("[addScheduleBlock] Creating schedule block with name: " + scheduleBlockDto.name());
        ScheduleBlock scheduleBlock = parseDtoToBlock(scheduleBlockDto);
        scheduleBlock = scheduleBlockRepository.save(scheduleBlock);
        logger.debug("[addScheduleBlock] Successfully created schedule block with name: " + scheduleBlockDto.name());
        return parseBlockToDto(scheduleBlock);
    }

    @Override
    public ScheduleBlockDto getScheduleBlock(Long scheduleBlockId) {
        logger.debug("[getScheduleBlock] Getting schedule block with id: " + scheduleBlockId);
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            logger.debug("[getScheduleBlock] Successfully fetched schedule block with id: " + scheduleBlockId);
            return parseBlockToDto(
                    scheduleBlock.get()
            );
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public ScheduleBlockDto updateScheduleBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException {
        logger.debug("[updateScheduleBlock] Updating schedule block with id: " + scheduleBlockDto.id());
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockDto.id());
        if (scheduleBlock.isPresent()) {
            logger.debug("[updateScheduleBlock] Successfully updated schedule block with id: " + scheduleBlockDto.id());
            return parseBlockToDto(
                    scheduleBlockRepository.save(
                            parseDtoToBlock(scheduleBlockDto)
                    )
            );
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void deleteScheduleBlock(Long scheduleBlockId) {
        logger.debug("[deleteScheduleBlock] Deleting schedule block with id: " + scheduleBlockId);
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            logger.debug("[deleteScheduleBlock] Successfully deleted schedule block with id: " + scheduleBlockId);
            scheduleBlockRepository.delete(scheduleBlock.get());
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<ScheduleBlockDto> getScheduleBlocksForScheduleByDay(Long scheduleTagId, String day) {
        logger.debug("[getScheduleBlocksForScheduleByDay] Getting schedule blocks for tag id: " + scheduleTagId + " and day: " + day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dayAsLocalDateTime = LocalDateTime.parse(day, formatter);
        LocalDateTime startOfDay = dayAsLocalDateTime.with(LocalTime.MIN);
        LocalDateTime endOfDay = dayAsLocalDateTime.with(LocalTime.MAX);

        List<ScheduleBlock> blockList = scheduleBlockRepository.findAllByScheduleTagIdAndStartDateBetweenOrderByStartDateAsc(scheduleTagId, startOfDay, endOfDay);
        logger.info("[getScheduleBlocksForScheduleByDay] Successfully fetched " + blockList.size() + " blocks");

        List<ScheduleBlockDto> dtoList = new ArrayList<>();

        for (ScheduleBlock block : blockList) {
            dtoList.add(parseBlockToDto(block));
        }

        return dtoList;
    }

    private ScheduleBlock parseDtoToBlock(ScheduleBlockDto dto) throws ParseException {
        logger.trace("[parseDtoToBlock] Parsing DTO to block");
        ScheduleBlock block = new ScheduleBlock();
        if (dto.id() != null) {
            block.setId(dto.id());
        }

        Optional<ScheduleTag> scheduleTag = scheduleTagRepository.findById(dto.scheduleTagId());

        if (scheduleTag.isPresent()) {
            block.setScheduleTag(scheduleTag.get());
        } else {
            throw new EntityNotFoundException();
        }

        block.setName(dto.name());

        // Parse startDate and endDate strings to java.util.Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(dto.startDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(dto.endDate(), formatter);

        block.setStartDate(startDate);
        block.setEndDate(endDate);

        logger.trace("[parseDtoToBlock] Successfully parsed DTO to block");

        return block;
    }

    private ScheduleBlockDto parseBlockToDto(ScheduleBlock block) {
        logger.trace("[parseDtoToBlock] Parsing block to DTO");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDate = block.getStartDate().format(formatter);
        String endDate = block.getEndDate().format(formatter);

        logger.trace("[parseDtoToBlock] Successfully parsed block to DTO");
        return new ScheduleBlockDto(
                block.getId(),
                block.getScheduleTag().getId(),
                block.getName(),
                startDate,
                endDate
        );
    }
}

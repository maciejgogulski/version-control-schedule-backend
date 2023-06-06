package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import com.maciejgogulski.eventschedulingbackend.dto.BlocksForScheduleByDayRequestDto;
import com.maciejgogulski.eventschedulingbackend.dto.ScheduleBlockDto;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleBlockRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleTagRepository;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleBlockService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ScheduleBlockServiceImpl implements ScheduleBlockService {

    private final ScheduleBlockRepository scheduleBlockRepository;

    private final ScheduleTagRepository scheduleTagRepository;

    public ScheduleBlockServiceImpl(ScheduleBlockRepository scheduleBlockRepository, ScheduleTagRepository scheduleTagRepository) {
        this.scheduleBlockRepository = scheduleBlockRepository;
        this.scheduleTagRepository = scheduleTagRepository;
    }

    @Override
    public ScheduleBlockDto addScheduleBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException {
        return parseBlockToDto(
                scheduleBlockRepository.save(
                        parseDtoToBlock(scheduleBlockDto)
                )
        );
    }

    @Override
    public ScheduleBlockDto getScheduleBlock(Long scheduleBlockId) {
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            return parseBlockToDto(
                    scheduleBlock.get()
            );
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public ScheduleBlockDto updateScheduleBlock(ScheduleBlockDto scheduleBlockDto) throws ParseException {
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockDto.id());
        if (scheduleBlock.isPresent()) {
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
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            scheduleBlockRepository.delete(scheduleBlock.get());
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<ScheduleBlockDto> getScheduleBlocksForScheduleByDay(Long scheduleTagId, Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        // Set the time components of the date to the end of the day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfDay = calendar.getTime();

        List<ScheduleBlock> blockList = scheduleBlockRepository.findAllByScheduleTagIdAndStartDateBetweenOrderByStartDateAsc(scheduleTagId, startOfDay, endOfDay);
        List<ScheduleBlockDto> dtoList = new ArrayList<>();

        for (ScheduleBlock block : blockList) {
            dtoList.add(
                    parseBlockToDto(block)
            );
        }

        return dtoList;
    }

    private ScheduleBlock parseDtoToBlock(ScheduleBlockDto dto) throws ParseException {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = dateFormat.parse(dto.startDate());
        Date endDate = dateFormat.parse(dto.endDate());

        block.setStartDate(startDate);
        block.setEndDate(endDate);

        return block;
    }

    private ScheduleBlockDto parseBlockToDto(ScheduleBlock block) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = dateFormat.format(block.getStartDate());
        String endDate = dateFormat.format(block.getEndDate());

        return new ScheduleBlockDto(
                block.getId(),
                block.getScheduleTag().getId(),
                block.getName(),
                startDate,
                endDate
        );
    }
}

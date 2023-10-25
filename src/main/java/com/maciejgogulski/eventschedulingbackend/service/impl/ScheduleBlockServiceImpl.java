package com.maciejgogulski.eventschedulingbackend.service.impl;

import com.maciejgogulski.eventschedulingbackend.dao.BlockParameterDao;
import com.maciejgogulski.eventschedulingbackend.domain.*;
import com.maciejgogulski.eventschedulingbackend.dto.ParameterDto;
import com.maciejgogulski.eventschedulingbackend.dto.ScheduleBlockDto;
import com.maciejgogulski.eventschedulingbackend.repositories.BlockParameterRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ParameterDictRepository;
import com.maciejgogulski.eventschedulingbackend.repositories.ScheduleBlockRepository;
import com.maciejgogulski.eventschedulingbackend.service.ModificationService;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleBlockService;
import com.maciejgogulski.eventschedulingbackend.service.ScheduleTagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

    private final ScheduleTagService scheduleTagService;

    private final ParameterDictRepository parameterDictRepository;

    private final BlockParameterRepository blockParameterRepository;

    private final BlockParameterDao blockParameterDao;

    private final ModificationService modificationService;

    public ScheduleBlockServiceImpl(
            ScheduleBlockRepository scheduleBlockRepository,
            ScheduleTagService scheduleTagService,
            ParameterDictRepository parameterDictRepository,
            BlockParameterRepository blockParameterRepository,
            BlockParameterDao blockParameterDao,
            ModificationService modificationService) {
        this.scheduleBlockRepository = scheduleBlockRepository;
        this.scheduleTagService = scheduleTagService;
        this.parameterDictRepository = parameterDictRepository;
        this.blockParameterRepository = blockParameterRepository;
        this.blockParameterDao = blockParameterDao;
        this.modificationService = modificationService;
    }

    @Override
    public ScheduleBlockDto addScheduleBlock(ScheduleBlockDto scheduleBlockDto) {
        logger.info("[addScheduleBlock] Creating schedule block with name: " + scheduleBlockDto.name());
        ScheduleBlock scheduleBlock = parseDtoToBlock(scheduleBlockDto);
        scheduleBlock = scheduleBlockRepository.save(scheduleBlock);
        logger.info("[addScheduleBlock] Successfully created schedule block with name: " + scheduleBlockDto.name());
        return parseBlockToDto(scheduleBlock);
    }

    @Override
    public ScheduleBlockDto getScheduleBlock(Long scheduleBlockId) {
        logger.info("[getScheduleBlock] Getting schedule block with id: " + scheduleBlockId);
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            logger.info("[getScheduleBlock] Successfully fetched schedule block with id: " + scheduleBlockId);
            return parseBlockToDto(
                    scheduleBlock.get()
            );
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public ScheduleBlockDto updateScheduleBlock(ScheduleBlockDto scheduleBlockDto) {
        logger.info("[updateScheduleBlock] Updating schedule block with id: " + scheduleBlockDto.id());
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockDto.id());
        if (scheduleBlock.isPresent()) {
            logger.info("[updateScheduleBlock] Successfully updated schedule block with id: " + scheduleBlockDto.id());
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
        logger.info("[deleteScheduleBlock] Deleting schedule block with id: " + scheduleBlockId);
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            logger.info("[deleteScheduleBlock] Successfully deleted schedule block with id: " + scheduleBlockId);
            scheduleBlockRepository.delete(scheduleBlock.get());
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<ScheduleBlockDto> getScheduleBlocksForScheduleByDay(Long scheduleTagId, String day) {
        logger.info("[getScheduleBlocksForScheduleByDay] Getting schedule blocks for tag id: " + scheduleTagId + " and day: " + day);
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

    private ScheduleBlock parseDtoToBlock(ScheduleBlockDto dto) {
        logger.trace("[parseDtoToBlock] Parsing DTO to block");
        ScheduleBlock block = new ScheduleBlock();
        if (dto.id() != null) {
            block.setId(dto.id());
        }

        ScheduleTag scheduleTag = scheduleTagService.getScheduleTag(dto.scheduleTagId());

        block.setScheduleTag(scheduleTag);
        block.setName(dto.name());

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

    @Override
    public void assignParameterToBlock(ParameterDto parameterDto) {
        String parameterName = parameterDto.parameterName();
        String parameterValue = parameterDto.value();
        Long blockId = parameterDto.scheduleBlockId();

        logger.info("[assignParameterToBlock] Assigning parameter with name: " + parameterName
                + " and value: " + parameterValue + " to schedule block with id: " + blockId);

        Optional<ParameterDict> parameterDictOpt = parameterDictRepository.findByName(parameterName);
        ParameterDict parameterDict;

        if (parameterDictOpt.isPresent()) {
            parameterDict = parameterDictOpt.get();
        } else {
            parameterDict = new ParameterDict();
            parameterDict.setName(parameterName);
            parameterDictRepository.save(parameterDict);
        }

        BlockParameter blockParameter = new BlockParameter();
        blockParameter.setScheduleBlock(
                scheduleBlockRepository.findById(blockId).orElseThrow(EntityNotFoundException::new)
        );
        blockParameter.setParameterDict(
                parameterDict
        );
        blockParameter.setValue(parameterValue);
        blockParameterRepository.save(blockParameter);

        modificationService.assignParameterToScheduleBlockModification(blockParameter);

        logger.info("[assignParameterToBlock] Successfully assigned parameter with name: " + parameterName + " to schedule block with id: " + blockId);
    }

    @Override
    @Transactional
    public void updateParameterWithinBlock(ParameterDto parameterDto) {
        logger.info("[updateParameterWithinBlock] Updating parameter with name: " + parameterDto.parameterName()
                + " and value: " + parameterDto.value() + " within schedule block with id: " + parameterDto.scheduleBlockId());

        BlockParameter blockParameter = blockParameterRepository.findById(parameterDto.id())
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue(parameterDto.value());
        blockParameter.setScheduleBlock(
                scheduleBlockRepository.findById(parameterDto.scheduleBlockId()).orElseThrow(EntityNotFoundException::new)
        );
        blockParameter.setParameterDict(
               parameterDictRepository.findByName(parameterDto.parameterName())
                       .orElseThrow(EntityNotFoundException::new)
        );

        blockParameterRepository.save(blockParameter);

        modificationService.updateParameterWithinBlockModification(blockParameter);

        logger.info("[updateParameterWithinBlock] Successfully updated parameter with name: " + parameterDto.parameterName() + " within schedule block with id: " + parameterDto.scheduleBlockId());
    }

    @Override
    @Transactional
    public void deleteParameterFormScheduleBlock(Long blockParameterId) {
        logger.info("[deleteParameterFormScheduleBlock] Deleting parameter id: " + blockParameterId);
        BlockParameter blockParameter = blockParameterRepository.findById(blockParameterId)
                .orElseThrow(EntityNotFoundException::new);
        modificationService.deleteParameterFromScheduleBlockModification(blockParameter);
        blockParameterRepository.deleteById(blockParameterId);
        logger.info("[deleteParameterFormScheduleBlock] Successfully deleted parameter id: " + blockParameterId);
    }

    @Override
    @Transactional
    public List<ParameterDto> getParametersForBlock(Long scheduleBlockId) {
        final String METHOD_TAG = "[getParametersForBlock]";
        logger.debug(METHOD_TAG + " Getting parameters for block with id: " + scheduleBlockId);

        List<ParameterDto> parameterDtoList = blockParameterDao.get_parameters_for_schedule_block(scheduleBlockId);

        logger.debug(METHOD_TAG + " Successfully fetched: " + parameterDtoList.size() + " parameters");
        return parameterDtoList;
    }

}

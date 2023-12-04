package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.dao.BlockParameterDao;
import com.maciejgogulski.versioncontrolschedule.domain.*;
import com.maciejgogulski.versioncontrolschedule.dto.ParameterDto;
import com.maciejgogulski.versioncontrolschedule.dto.ScheduleBlockDto;
import com.maciejgogulski.versioncontrolschedule.repositories.BlockParameterRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.ParameterDictRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.ScheduleBlockRepository;
import com.maciejgogulski.versioncontrolschedule.service.ModificationService;
import com.maciejgogulski.versioncontrolschedule.service.ScheduleBlockService;
import com.maciejgogulski.versioncontrolschedule.service.ScheduleTagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    @Transactional
    public ScheduleBlockDto addBlock(ScheduleBlockDto scheduleBlockDto) {
        logger.info("[addScheduleBlock] Creating schedule block with name: " + scheduleBlockDto.name());
        
        Block block = parseDtoToBlock(scheduleBlockDto);
        block = scheduleBlockRepository.save(block);

        assignRequiredParameters(block);

        logger.info("[addScheduleBlock] Successfully created schedule block with name: " + scheduleBlockDto.name());
        return parseBlockToDto(block);
    }

    @Override
    @Transactional
    public void assignRequiredParameters(Block block) {
        assignParameterToBlock(new ParameterDto(
                null,
                block.getId(),
                "Name",
                block.getName()
        ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        assignParameterToBlock(new ParameterDto(
                null,
                block.getId(),
                "Start date",
                formatter.format(block.getStartDate())
        ));

        assignParameterToBlock(new ParameterDto(
                null,
                block.getId(),
                "End date",
                formatter.format(block.getEndDate())
        ));
    }

    @Override
    public ScheduleBlockDto getBlock(Long scheduleBlockId) {
        logger.info("[getScheduleBlock] Getting schedule block with id: " + scheduleBlockId);
        Optional<Block> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

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
    public ScheduleBlockDto updateBlock(ScheduleBlockDto scheduleBlockDto) {
        logger.info("[updateScheduleBlock] Updating schedule block with id: " + scheduleBlockDto.id());
        Optional<Block> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockDto.id());
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
    public void deleteBlock(Long scheduleBlockId) {
        logger.info("[deleteScheduleBlock] Deleting schedule block with id: " + scheduleBlockId);
        Optional<Block> scheduleBlock = scheduleBlockRepository.findById(scheduleBlockId);

        if (scheduleBlock.isPresent()) {
            logger.info("[deleteScheduleBlock] Successfully deleted schedule block with id: " + scheduleBlockId);
            scheduleBlockRepository.delete(scheduleBlock.get());
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<ScheduleBlockDto> getBlocksForScheduleByDay(Long scheduleTagId, String day) {
        logger.info("[getScheduleBlocksForScheduleByDay] Getting schedule blocks for tag id: " + scheduleTagId + " and day: " + day);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dayAsLocalDateTime = LocalDateTime.parse(day, formatter);
        LocalDateTime startOfDay = dayAsLocalDateTime.with(LocalTime.MIN);
        LocalDateTime endOfDay = dayAsLocalDateTime.with(LocalTime.MAX);

        List<Block> blockList = scheduleBlockRepository.findAllByScheduleTagIdAndStartDateBetweenOrderByStartDateAsc(scheduleTagId, startOfDay, endOfDay);
        logger.info("[getScheduleBlocksForScheduleByDay] Successfully fetched " + blockList.size() + " blocks");

        List<ScheduleBlockDto> dtoList = new ArrayList<>();

        for (Block block : blockList) {
            dtoList.add(parseBlockToDto(block));
        }

        return dtoList;
    }

    private Block parseDtoToBlock(ScheduleBlockDto dto) {
        logger.trace("[parseDtoToBlock] Parsing DTO to block");
        Block block = new Block();
        if (dto.id() != null) {
            block.setId(dto.id());
        }

        Schedule schedule = scheduleTagService.getScheduleTag(dto.scheduleTagId());

        block.setSchedule(schedule);
        block.setName(dto.name());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(dto.startDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(dto.endDate(), formatter);

        block.setStartDate(startDate);
        block.setEndDate(endDate);

        return block;
    }

    private ScheduleBlockDto parseBlockToDto(Block block) {
        logger.trace("[parseDtoToBlock] Parsing block to DTO");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDate = block.getStartDate().format(formatter);
        String endDate = block.getEndDate().format(formatter);

        return new ScheduleBlockDto(
                block.getId(),
                block.getSchedule().getId(),
                block.getName(),
                startDate,
                endDate
        );
    }

    @Override
    @Transactional
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

        Block block = scheduleBlockRepository.findById(blockId)
                 .orElseThrow(EntityNotFoundException::new);

        BlockParameter blockParameter = blockParameterRepository
                .find_deleted_block_parameter_by_block_id_parameter_dict_pair(block.getId(), parameterDict.getId())
                .orElse(new BlockParameter());

        blockParameter.setBlock(
                block
        );
        blockParameter.setParameterDict(
                parameterDict
        );
        blockParameter.setValue(parameterValue);

        blockParameter.setDeleted(false);

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
        blockParameter.setBlock(
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
    public void deleteParameterFromBlock(Long blockParameterId) {
        logger.info("[deleteParameterFormScheduleBlock] Deleting parameter id: " + blockParameterId);
        BlockParameter blockParameter = blockParameterRepository.findById(blockParameterId)
                .orElseThrow(EntityNotFoundException::new);
        blockParameterRepository.delete_block_parameter(blockParameterId);
        modificationService.deleteParameterFromScheduleBlockModification(blockParameter);
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

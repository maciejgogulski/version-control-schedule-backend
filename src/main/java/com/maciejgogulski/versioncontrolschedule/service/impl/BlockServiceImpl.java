package com.maciejgogulski.versioncontrolschedule.service.impl;

import com.maciejgogulski.versioncontrolschedule.dao.BlockParameterDao;
import com.maciejgogulski.versioncontrolschedule.domain.*;
import com.maciejgogulski.versioncontrolschedule.dto.ParameterDto;
import com.maciejgogulski.versioncontrolschedule.dto.BlockDto;
import com.maciejgogulski.versioncontrolschedule.repositories.BlockParameterRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.ParameterDictRepository;
import com.maciejgogulski.versioncontrolschedule.repositories.BlockRepository;
import com.maciejgogulski.versioncontrolschedule.service.ModificationService;
import com.maciejgogulski.versioncontrolschedule.service.BlockService;
import com.maciejgogulski.versioncontrolschedule.service.ScheduleService;
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
public class BlockServiceImpl implements BlockService {

    private final Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

    private final BlockRepository blockRepository;

    private final ScheduleService scheduleService;

    private final ParameterDictRepository parameterDictRepository;

    private final BlockParameterRepository blockParameterRepository;

    private final BlockParameterDao blockParameterDao;

    private final ModificationService modificationService;

    public BlockServiceImpl(
            BlockRepository blockRepository,
            ScheduleService scheduleService,
            ParameterDictRepository parameterDictRepository,
            BlockParameterRepository blockParameterRepository,
            BlockParameterDao blockParameterDao,
            ModificationService modificationService) {
        this.blockRepository = blockRepository;
        this.scheduleService = scheduleService;
        this.parameterDictRepository = parameterDictRepository;
        this.blockParameterRepository = blockParameterRepository;
        this.blockParameterDao = blockParameterDao;
        this.modificationService = modificationService;
    }
    
    @Override
    @Transactional
    public BlockDto addBlock(BlockDto blockDto) {
        logger.info("[addBlock] Creating block with name: " + blockDto.name());
        
        Block block = parseDtoToBlock(blockDto);
        block = blockRepository.save(block);

        assignRequiredParameters(block);

        logger.info("[addBlock] Successfully created block with name: " + blockDto.name());
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
    public BlockDto getBlock(Long blockId) {
        logger.info("[getBlock] Getting block with id: " + blockId);
        Optional<Block> block = blockRepository.findById(blockId);

        if (block.isPresent()) {
            logger.info("[getBlock] Successfully fetched block with id: " + blockId);
            return parseBlockToDto(
                    block.get()
            );
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public BlockDto updateBlock(BlockDto blockDto) {
        logger.info("[updateBlock] Updating block with id: " + blockDto.id());
        Optional<Block> block = blockRepository.findById(blockDto.id());
        if (block.isPresent()) {
            logger.info("[updateBlock] Successfully updated block with id: " + blockDto.id());
            return parseBlockToDto(
                    blockRepository.save(
                            parseDtoToBlock(blockDto)
                    )
            );
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void deleteBlock(Long blockId) {
        logger.info("[deleteBlock] Deleting block with id: " + blockId);
        Optional<Block> block = blockRepository.findById(blockId);

        if (block.isPresent()) {
            logger.info("[deleteBlock] Successfully deleted block with id: " + blockId);
            blockRepository.delete(block.get());
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<BlockDto> getBlocksForScheduleByDay(Long scheduleId, String day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dayAsLocalDateTime = LocalDateTime.parse(day, formatter);
        LocalDateTime startOfDay = dayAsLocalDateTime.with(LocalTime.MIN);
        LocalDateTime endOfDay = dayAsLocalDateTime.with(LocalTime.MAX);

        List<Block> blockList = blockRepository.findAllByScheduleIdAndStartDateBetweenOrderByStartDateAsc(scheduleId, startOfDay, endOfDay);

        List<BlockDto> dtoList = new ArrayList<>();

        for (Block block : blockList) {
            dtoList.add(parseBlockToDto(block));
        }

        return dtoList;
    }

    private Block parseDtoToBlock(BlockDto dto) {
        logger.trace("[parseDtoToBlock] Parsing DTO to block");
        Block block = new Block();
        if (dto.id() != null) {
            block.setId(dto.id());
        }

        Schedule schedule = scheduleService.getSchedule(dto.scheduleId());

        block.setSchedule(schedule);
        block.setName(dto.name());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(dto.startDate(), formatter);
        LocalDateTime endDate = LocalDateTime.parse(dto.endDate(), formatter);

        block.setStartDate(startDate);
        block.setEndDate(endDate);

        return block;
    }

    private BlockDto parseBlockToDto(Block block) {
        logger.trace("[parseDtoToBlock] Parsing block to DTO");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDate = block.getStartDate().format(formatter);
        String endDate = block.getEndDate().format(formatter);

        return new BlockDto(
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
        Long blockId = parameterDto.blockId();

        logger.info("[assignParameterToBlock] Assigning parameter with name: " + parameterName
                + " and value: " + parameterValue + " to block with id: " + blockId);

        Optional<ParameterDict> parameterDictOpt = parameterDictRepository.findByName(parameterName);
        ParameterDict parameterDict;

        if (parameterDictOpt.isPresent()) {
            parameterDict = parameterDictOpt.get();
        } else {
            parameterDict = new ParameterDict();
            parameterDict.setName(parameterName);
            parameterDictRepository.save(parameterDict);
        }

        Block block = blockRepository.findById(blockId)
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

        modificationService.assignParameterToScheduleModification(blockParameter);

        logger.info("[assignParameterToBlock] Successfully assigned parameter with name: " + parameterName + " to block with id: " + blockId);
    }

    @Override
    @Transactional
    public void updateParameterWithinBlock(ParameterDto parameterDto) {
        logger.info("[updateParameterWithinBlock] Updating parameter with name: " + parameterDto.parameterName()
                + " and value: " + parameterDto.value() + " within block with id: " + parameterDto.blockId());

        BlockParameter blockParameter = blockParameterRepository.findById(parameterDto.id())
                .orElseThrow(EntityNotFoundException::new);

        blockParameter.setValue(parameterDto.value());
        blockParameter.setBlock(
                blockRepository.findById(parameterDto.blockId()).orElseThrow(EntityNotFoundException::new)
        );
        blockParameter.setParameterDict(
               parameterDictRepository.findByName(parameterDto.parameterName())
                       .orElseThrow(EntityNotFoundException::new)
        );

        blockParameterRepository.save(blockParameter);

        modificationService.updateParameterWithinBlockModification(blockParameter);

        logger.info("[updateParameterWithinBlock] Successfully updated parameter with name: " + parameterDto.parameterName() + " within block with id: " + parameterDto.blockId());
    }

    @Override
    @Transactional
    public void deleteParameterFromBlock(Long blockParameterId) {
        logger.info("[deleteParameterFromBlock] Deleting parameter id: " + blockParameterId);
        BlockParameter blockParameter = blockParameterRepository.findById(blockParameterId)
                .orElseThrow(EntityNotFoundException::new);
        blockParameterRepository.delete_block_parameter(blockParameterId);
        modificationService.deleteParameterFromBlockModification(blockParameter);
        logger.info("[deleteParameterFromBlock] Successfully deleted parameter id: " + blockParameterId);
    }

    @Override
    @Transactional
    public List<ParameterDto> getParametersForBlock(Long blockId) {
        List<ParameterDto> parameterDtoList = blockParameterDao.get_parameters_for_block(blockId);
        return parameterDtoList;
    }

}

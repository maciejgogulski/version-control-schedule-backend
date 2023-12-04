package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.domain.Block;
import com.maciejgogulski.versioncontrolschedule.dto.ParameterDto;
import com.maciejgogulski.versioncontrolschedule.dto.BlockDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public interface BlockService {

    BlockDto addBlock(BlockDto blockDto) throws ParseException;

    void assignRequiredParameters(Block block);

    BlockDto getBlock(Long blockId);

    BlockDto updateBlock(BlockDto blockDto) throws ParseException;

    void deleteBlock(Long blockId);

    List<BlockDto> getBlocksForScheduleByDay(Long scheduleId, String day);

    void assignParameterToBlock(ParameterDto parameterDto);

    List<ParameterDto> getParametersForBlock(Long blockId);

    void updateParameterWithinBlock(ParameterDto parameterDto);

    void deleteParameterFromBlock(Long blockParameterId);
}

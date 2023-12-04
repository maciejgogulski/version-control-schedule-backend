package com.maciejgogulski.versioncontrolschedule.service;

import com.maciejgogulski.versioncontrolschedule.dto.AddresseeDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;
import java.util.List;

@Service
public interface AddresseeService {
    @Transactional
    List<AddresseeDto> getAddressesByScheduleId(Long scheduleTagId);

    @Transactional
    void assignAddresseeToSchedule(Long addresseeId, Long scheduleTagId) throws AlreadyBoundException;
}

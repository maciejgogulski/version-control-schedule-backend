package com.maciejgogulski.eventschedulingbackend.service;

import com.maciejgogulski.eventschedulingbackend.dto.AddresseeDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;
import java.util.List;

@Service
public interface AddresseeService {
    @Transactional
    List<AddresseeDto> getAddressesByScheduleTagId(Long scheduleTagId);

    @Transactional
    void assignAddresseeToScheduleTagId(Long addresseeId, Long scheduleTagId) throws AlreadyBoundException;
}

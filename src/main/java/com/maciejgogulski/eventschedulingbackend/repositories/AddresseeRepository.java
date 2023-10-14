package com.maciejgogulski.eventschedulingbackend.repositories;

import com.maciejgogulski.eventschedulingbackend.domain.Addressee;
import com.maciejgogulski.eventschedulingbackend.domain.ScheduleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface AddresseeRepository extends JpaRepository<Addressee, Long> {
    @Procedure()
    List<Addressee> get_addressees_for_schedule_tag(Long scheduleTagId);
}

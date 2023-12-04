package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.Addressee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;

public interface AddresseeRepository extends JpaRepository<Addressee, Long> {
    @Procedure
    List<Addressee> get_addressees_for_schedule_tag(Long scheduleTagId);

    @Procedure
    void assign_addressee_to_schedule_tag(Long addresseeId, Long scheduleTagId);
}

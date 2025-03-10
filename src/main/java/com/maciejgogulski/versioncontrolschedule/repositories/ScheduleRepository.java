package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}

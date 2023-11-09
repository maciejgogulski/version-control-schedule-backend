package com.maciejgogulski.eventschedulingbackend.repositories;

import com.maciejgogulski.eventschedulingbackend.domain.BlockParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockParameterRepository extends JpaRepository<BlockParameter, Long> {

    @Procedure
    void delete_block_parameter(Long blockParameterId);
}

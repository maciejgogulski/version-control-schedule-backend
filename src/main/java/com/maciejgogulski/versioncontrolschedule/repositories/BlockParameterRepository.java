package com.maciejgogulski.versioncontrolschedule.repositories;

import com.maciejgogulski.versioncontrolschedule.domain.BlockParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockParameterRepository extends JpaRepository<BlockParameter, Long> {

    @Procedure
    void delete_block_parameter(Long blockParameterId);

    @Procedure
    Optional<BlockParameter> find_deleted_block_parameter_by_block_id_parameter_dict_pair(Long blockId, Long parameterDictId);
}

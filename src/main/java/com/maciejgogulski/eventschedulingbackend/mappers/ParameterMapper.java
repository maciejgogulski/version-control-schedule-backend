package com.maciejgogulski.eventschedulingbackend.mappers;

import com.maciejgogulski.eventschedulingbackend.dto.ParameterDto;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class ParameterMapper {

    public ParameterDto mapRow(SqlRowSet rowSet) {
        return new ParameterDto(
                rowSet.getLong("id"),
                rowSet.getLong("schedule_block_id"),
                rowSet.getString("parameter_name"),
                rowSet.getString("value")
        );
    }
}

package com.maciejgogulski.versioncontrolschedule.mappers;

import com.maciejgogulski.versioncontrolschedule.dto.ParameterDto;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class ParameterMapper {

    public ParameterDto mapRow(SqlRowSet rowSet) {
        return new ParameterDto(
                rowSet.getLong("id"),
                rowSet.getLong("block_id"),
                rowSet.getString("parameter_name"),
                rowSet.getString("value")
        );
    }
}

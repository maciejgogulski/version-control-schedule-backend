package com.maciejgogulski.eventschedulingbackend.mappers;

import com.maciejgogulski.eventschedulingbackend.dto.ModificationDto;
import com.maciejgogulski.eventschedulingbackend.enums.ModificationType;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class ModificationMapper {

    public ModificationDto mapRow(SqlRowSet rowSet) {
        return new ModificationDto(
                rowSet.getLong("id"),
                rowSet.getLong("staged_event_id"),
                rowSet.getLong("block_parameter_id"),
                rowSet.getString("block_name"),
                rowSet.getString("parameter_name"),
                ModificationType.valueOf(rowSet.getString("type")),
                rowSet.getString("old_value"),
                rowSet.getString("new_value"),
                (rowSet.getTimestamp("timestamp") != null)
                        ? rowSet.getTimestamp("timestamp").toLocalDateTime()
                        : null
        );
    }
}

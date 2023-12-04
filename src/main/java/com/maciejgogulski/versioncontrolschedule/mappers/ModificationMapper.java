package com.maciejgogulski.versioncontrolschedule.mappers;

import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class ModificationMapper {

    public ModificationDto mapRow(SqlRowSet rowSet) {
        return new ModificationDto(
                rowSet.getLong("id"),
                rowSet.getLong("version_id"),
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

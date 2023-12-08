package com.maciejgogulski.versioncontrolschedule.mappers;

import com.maciejgogulski.versioncontrolschedule.domain.Modification;
import com.maciejgogulski.versioncontrolschedule.domain.Version;
import com.maciejgogulski.versioncontrolschedule.dto.ModificationDto;
import com.maciejgogulski.versioncontrolschedule.enums.ModificationType;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class ModificationMapper {

    public Modification mapRow(SqlRowSet rowSet) {
        Modification modification = new Modification();
        modification.setId(rowSet.getLong("modification_id"));
        modification.setType(rowSet.getString("modification_type"));
        modification.setOldValue(rowSet.getString("modification_old_value"));
        modification.setNewValue(rowSet.getString("modification_new_value"));
        modification.setTimestamp(
                (rowSet.getTimestamp("modification_timestamp") != null)
                        ? rowSet.getTimestamp("modification_timestamp").toLocalDateTime()
                        : null
        );

        Version version = new Version();
        version.setId(rowSet.getLong("version_id"));
        version.setCommitted(rowSet.getBoolean("version_committed"));
        version.setTimestamp(
                (rowSet.getTimestamp("version_timestamp") != null)
                        ? rowSet.getTimestamp("version_timestamp").toLocalDateTime()
                        : null
        );

        modification.setVersion(version);

        return modification;
    }
}

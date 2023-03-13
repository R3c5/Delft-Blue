package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * The type Sysadmin schedule dto.
 */
@Data
public class SysadminScheduleDTO {
    private Map<FacultyName, List<RequestDTO>> schedule;

    /**
     * Instantiates a new Sysadmin schedule dto.
     *
     * @param schedule the schedule
     */
    public SysadminScheduleDTO(@JsonProperty("schedule") Map<FacultyName, List<RequestDTO>> schedule) {
        this.schedule = schedule;
    }
}

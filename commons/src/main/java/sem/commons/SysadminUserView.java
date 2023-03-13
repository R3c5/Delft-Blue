package sem.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SysadminUserView {

    private SysadminResourceManagerView sysadminResourceManagerView;

    private SysadminScheduleDTO sysadminScheduleDTO;

    public SysadminUserView(
            @JsonProperty("sysadminResourceManagerView") SysadminResourceManagerView sysadminResourceManagerView,
            @JsonProperty("sysadminScheduleDTO") SysadminScheduleDTO sysadminScheduleDTO
    ) {
        this.sysadminScheduleDTO = sysadminScheduleDTO;
        this.sysadminResourceManagerView = sysadminResourceManagerView;
    }
}

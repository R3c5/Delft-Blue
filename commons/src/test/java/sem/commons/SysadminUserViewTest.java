package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SysadminUserViewTest {

    SysadminResourceManagerView sysadminResourceManagerView;
    SysadminScheduleDTO sysadminScheduleDTO;
    SysadminUserView  sysadminUserView;
    Map<FacultyName, List<RequestDTO>> map;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        map = new HashMap<>();
        RequestDTO requestDTO = new RequestDTO(1L, "name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1));
        map.put(FacultyName.EEMCS, List.of(requestDTO));
        sysadminScheduleDTO = new SysadminScheduleDTO(map);
        sysadminResourceManagerView = new SysadminResourceManagerView(new HashMap<>(),
                new Resource(3, 2, 1), new ArrayList<>());
        sysadminUserView = new SysadminUserView(sysadminResourceManagerView, sysadminScheduleDTO);
    }

    @Test
    void notNull() {
        assertNotNull(sysadminUserView);
        assertEquals(sysadminScheduleDTO, sysadminUserView.getSysadminScheduleDTO());
        assertEquals(sysadminResourceManagerView, sysadminUserView.getSysadminResourceManagerView());
    }
}
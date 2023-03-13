package sem.commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SysadminScheduleDTOTest {

    SysadminScheduleDTO sysadminScheduleDTO;
    Map<FacultyName, List<RequestDTO>> map;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        map = new HashMap<>();
        RequestDTO requestDTO = new RequestDTO(1L, "name", "netId", FacultyName.EEMCS, "desc",
                LocalDate.of(2015, 2, 3), new Resource(1, 1, 1));
        map.put(FacultyName.EEMCS, List.of(requestDTO));
        sysadminScheduleDTO = new SysadminScheduleDTO(map);
    }

    @Test
    void notNull() {
        assertNotNull(sysadminScheduleDTO);
        assertEquals(map, sysadminScheduleDTO.getSchedule());
    }
}
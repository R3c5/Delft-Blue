package sem.faculty.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.*;
import sem.faculty.domain.Request;
import sem.commons.*;
import sem.faculty.handler.FacultyHandlerService;

import java.time.LocalDate;

/**
 * The type Main faculty controller.
 */
@RestController
public class MainFacultyController {

    private transient FacultyHandlerService facultyHandlerService;
    final transient String groupId = "default";

    /**
     * Instantiates a new Main faculty controller.
     *
     * @param facultyHandlerService the faculty handler service
     */
    @Autowired
    public MainFacultyController(FacultyHandlerService facultyHandlerService) {
        this.facultyHandlerService = facultyHandlerService;
    }

    /**
     * Listen for incoming Requests.
     */
    @KafkaListener(
            topics = "incoming-request",
            groupId = groupId,
            containerFactory = "kafkaListenerContainerFactoryRequestDTO"
    )
    @SendTo
    public StatusDTO listener(RequestDTO request) {
        return facultyHandlerService.requestListener(request);
    }

    /**
     * Gets pending requests.
     *
     * @param facultyNameDTO the faculty name dto
     * @return the pending requests
     */
    @KafkaListener(
            topics = "pendingRequestsTopic",
            groupId = groupId,
            containerFactory = "kafkaListenerContainerFactoryFacultyName"
    )
    @SendTo
    public PendingRequestsDTO getPendingRequests(FacultyNameDTO facultyNameDTO) {
        System.out.println("got a faculty name " + facultyNameDTO.getFacultyName());
        return facultyHandlerService.getPendingRequests(facultyNameDTO.getFacultyName());
    }

    /**
     * Accept requests status dto.
     *
     * @param acceptRequestsDTO the accept requests dto
     * @return the status dto
     */
    @KafkaListener(
            topics = "acceptRequestsTopic",
            groupId = groupId,
            containerFactory = "kafkaListenerContainerFactoryAcceptRequests"
    )
    @SendTo
    public StatusDTO acceptRequests(AcceptRequestsDTO acceptRequestsDTO) {
        System.out.println("got a list of request IDs to accept " + acceptRequestsDTO.getAcceptedRequests().toString() +
                " for faculty " + acceptRequestsDTO.getFacultyName());
        return facultyHandlerService.acceptRequests(
                acceptRequestsDTO.getFacultyName(), acceptRequestsDTO.getAcceptedRequests()
        );
    }

    /**
     * Gets schedule for date.
     *
     * @param dateDTO the date dto
     * @return the schedule for date
     */
    @KafkaListener(
            topics = "sysadmin-view-faculty",
            groupId = groupId,
            containerFactory = "kafkaListenerContainerFactoryDateDTO"
    )
    @SendTo
    public SysadminScheduleDTO getScheduleForDate(DateDTO dateDTO) {
        return facultyHandlerService.getScheduleForDate(
                LocalDate.of(dateDTO.getYear(), dateDTO.getMonth(), dateDTO.getDay())
        );
    }
}

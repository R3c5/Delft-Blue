package sem.faculty.domain.scheduler;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sem.commons.FacultyName;
import sem.commons.NotificationDTO;
import sem.commons.ScheduleDateDTO;
import sem.commons.StatusDTO;
import sem.faculty.controllers.ScheduleRequestController;
import sem.faculty.domain.Faculty;
import sem.faculty.domain.Request;
import sem.faculty.domain.RequestRepository;
import sem.faculty.domain.RequestStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Strategy to mark requests as accepted and save them in the schedule in the faculty.
 */
@Service
public class AcceptRequestsScheduler extends SchedulableRequestsScheduler {

    public AcceptRequestsScheduler(ScheduleRequestController controller,
                                   RequestRepository requestRepository,
                                   KafkaTemplate<String, NotificationDTO> kafkaTemplate) {
        super(controller, requestRepository, kafkaTemplate);
    }

    @Override
    void saveRequestInFaculty(Request request, Faculty faculty, LocalDate date) {
        long requestID = request.getRequestId();

        boolean reserved = reserveResource(request, date, faculty.getFacultyName());
        if (!reserved) {
            request.setStatus(RequestStatus.DENIED);
            if (Objects.equals(requestRepository.findByRequestId(requestID), request)) {
                requestRepository.delete(requestRepository.findByRequestId(requestID));
            }
            return;
        }

        request.setStatus(RequestStatus.ACCEPTED);
        super.getKafkaTemplate().send("publish-notification",
                new NotificationDTO(request.getRequestFacultyInformation().getNetId(),
                        "Your request with name  " + request.getRequestResourceManagerInformation().getName() +
                                " has been accepted"));
        // update request repository
        if (Objects.equals(requestRepository.findByRequestId(requestID), request)) {
            requestRepository.updateRequestStatusAccepted(requestID);
        } else {
            requestRepository.saveAndFlush(request);
        }

        faculty.scheduleForDate(request, date);

    }

    /**
     * Reserve resources in Resource Manager.
     * @param request - Request for which to reserve the resources
     * @param date - LocalDate on which to reserve the resources
     * @param facultyName - FacultyName to which the request belongs
     * @return true iff the resources have been reserved successfully.
     */
    private boolean reserveResource(Request request, LocalDate date, FacultyName facultyName) {
        ScheduleRequestController controller = this.getController();

        ScheduleDateDTO scheduleDateDTO = new ScheduleDateDTO(
                request.getRequestResourceManagerInformation().getResource(), date, facultyName);

        StatusDTO response = controller.sendReserveResources(scheduleDateDTO);
        if (response.getStatus().equals("OK")) {
            return true;
        }

        getKafkaTemplate().send("publish-notification", new NotificationDTO(
                request.getRequestFacultyInformation().getNetId(),
                "Could not schedule request with name " +
                        request.getRequestResourceManagerInformation().getName() +
                        " because " + response.getStatus()));
        return false;
    }
}

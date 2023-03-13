package sem.faculty.domain.scheduler;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sem.commons.NotificationDTO;
import sem.faculty.controllers.ScheduleRequestController;
import sem.faculty.domain.Faculty;
import sem.faculty.domain.Request;
import sem.faculty.domain.RequestRepository;
import sem.faculty.domain.RequestStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Strategy to mark requests as pending and save them in the pending queue in the faculty.
 */
@Service
public class PendingRequestsScheduler extends SchedulableRequestsScheduler {

    public PendingRequestsScheduler(ScheduleRequestController controller,
                                    RequestRepository requestRepository,
                                    KafkaTemplate<String, NotificationDTO> kafkaTemplate) {
        super(controller, requestRepository, kafkaTemplate);
    }

    @Override
    void saveRequestInFaculty(Request request, Faculty faculty, LocalDate date) {
        request.setStatus(RequestStatus.PENDING);

        // update request repository
        long requestID = request.getRequestId();
        if (Objects.equals(requestRepository.findByRequestId(requestID), request)) {
            requestRepository.updateRequestStatusPending(requestID);
        } else {
            requestRepository.saveAndFlush(request);
        }

        faculty.addPendingRequest(request);
    }
}

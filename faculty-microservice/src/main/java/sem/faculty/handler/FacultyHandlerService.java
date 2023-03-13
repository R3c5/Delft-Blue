package sem.faculty.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sem.commons.*;
import sem.faculty.domain.Request;
import sem.faculty.domain.RequestDetails;
import sem.faculty.domain.RequestRepository;
import sem.faculty.domain.RequestStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Faculty manager service.
 */
@Service
public class FacultyHandlerService {


    private transient FacultyHandler facultyHandler;
    public final transient RequestRepository requestRepository;


    /**
     * Instantiates a new Faculty manager service.
     *
     * @param facultyHandler the faculty manager
     * @param requestRepository - Repository that stores all the requests that belong to all the faculties.
     */
    @Autowired
    public FacultyHandlerService(FacultyHandler facultyHandler,
                                  RequestRepository requestRepository) {
        this.facultyHandler = facultyHandler;
        this.requestRepository = requestRepository;
    }


    /**
     * Converts the RequestDTO to a regular pending request Object and forwards it to the specific faculty.
     *
     * @param request the RequestDTO sent from the MainFacultyController
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public StatusDTO requestListener(RequestDTO request) {
        String requestName = request.getRequestResourceManagerInformation().getName();
        String requestNetId = request.getRequestFacultyInformation().getNetId();
        String requestDescription = request.getRequestResourceManagerInformation().getDescription();
        LocalDate requestDate = request.getRequestFacultyInformation().getPreferredDate();
        Resource requestResources = request.getRequestResourceManagerInformation().getResource();

        try {
            requestResources.checkResourceValidity(
                requestResources.getCpu(), requestResources.getGpu(), requestResources.getMemory());
        } catch (NotValidResourcesException e) {
            return new StatusDTO(e.getMessage());
        }
        if (!requestDate.isAfter(facultyHandler.getCurrentDate())) {
            return new StatusDTO("You cannot schedule requests for today or the past!");
        }
        RequestDetails reqDet = new RequestDetails(requestName, requestDescription, requestDate, RequestStatus.PENDING);
        Request newRequest = new Request(reqDet, requestNetId,
                request.getRequestFacultyInformation().getFaculty(), requestResources);
        facultyHandler.handleIncomingRequests(newRequest);

        return new StatusDTO("OK");
    }



    /**
     * Deletes the request from the repository if it was dropped or denied.
     *
     * @param request - Request that has to be deleted from the repository
     */
    public void deleteRequest(Request request) {
        long requestID = request.getRequestId();
        Request requestFound = requestRepository.findByRequestId(requestID);
        if (requestFound.equals(request)) {
            requestRepository.delete(requestFound);
        }
    }


    /**
     * Makes changes to the requestRepository if the request was accepted.
     *
     * @param request - Request that has been accepted.
     */
    public void acceptRequest(Request request) {
        long requestID = request.getRequestId();
        Request requestFound = requestRepository.findByRequestId(requestID);
        if (requestFound.equals(request)) {
            requestRepository.updateRequestStatusAccepted(request.getRequestId());
        } else {
            requestRepository.saveAndFlush(request);
        }
    }

    /**
     * Gets pending requests.
     *
     * @param facultyName the faculty name
     * @return the pending requests
     */
    public PendingRequestsDTO getPendingRequests(String facultyName) {

        try {
            FacultyName.valueOf(facultyName);
        } catch (IllegalArgumentException e) {
            return new PendingRequestsDTO("Wrong faculty name", new ArrayList<>());
        }
        return new PendingRequestsDTO("OK",
                facultyHandler.getPendingRequests(FacultyName.valueOf(facultyName)).stream()
                        .map(x -> new RequestDTO(
                                x.getRequestId(),
                                x.getRequestResourceManagerInformation().getName(),
                                x.getRequestFacultyInformation().getNetId(),
                                x.getRequestFacultyInformation().getFaculty(),
                                x.getRequestResourceManagerInformation().getDescription(),
                                x.getRequestFacultyInformation().getPreferredDate(),
                                x.getRequestResourceManagerInformation().getResource()))
                        .collect(Collectors.toList()));

    }

    /**
     * Accept requests status dto.
     *
     * @param facultyName      the faculty name
     * @param acceptedRequests the accepted requests
     * @return the status dto
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public StatusDTO acceptRequests(String facultyName, List<Long> acceptedRequests) {
        FacultyName facName;
        try {
            facName = FacultyName.valueOf(facultyName);
        } catch (IllegalArgumentException e) {
            return new StatusDTO("Wrong faculty name");
        }
        List<Long> badRequests = acceptRequestsHelper(facName, acceptedRequests);
        return acceptRequestsOutput(badRequests);
    }

    /**
     * Helper method for Accept requests status dto.
     *
     * @param facName          the faculty name
     * @param acceptedRequests the accepted requests
     * @return the list of bad requests
     */
    public List<Long> acceptRequestsHelper(FacultyName facName, List<Long> acceptedRequests) {
        List<Long> badRequests = new ArrayList<>();
        for (Long id : acceptedRequests) {
            Request request = requestRepository.findByRequestId(id);
            if (request == null) {
                badRequests.add(id);
                continue;
            }
            facultyHandler.handleAcceptedRequests(facName, request);
        }
        return badRequests;
    }

    /**
     * Helps acceptRequests decide on the returned StatusDTO.
     *
     * @param badRequests the accepted requests
     * @return the StatusDTO to be sent
     */
    public StatusDTO acceptRequestsOutput(List<Long> badRequests) {
        if (badRequests.isEmpty()) {
            return new StatusDTO("OK");
        }
        return new StatusDTO("Could not find the following requests: " + badRequests.toString());
    }

    public SysadminScheduleDTO getScheduleForDate(LocalDate date) {
        return new SysadminScheduleDTO(facultyHandler.getRequestForDate(date));
    }

    /**
     * Schedule all pending requests for next day in all faculties.
     */
    public void acceptPendingRequestsForTomorrow() {
        facultyHandler.acceptPendingRequestsForTomorrow();
    }
}

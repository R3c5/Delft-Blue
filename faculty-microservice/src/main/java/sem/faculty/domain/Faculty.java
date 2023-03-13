package sem.faculty.domain;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sem.commons.FacultyName;
import sem.commons.RequestDTO;
import sem.faculty.provider.TimeProvider;

@Getter
public class   Faculty {
    private FacultyName facultyName;
    private Map<LocalDate, List<Request>> schedule;
    private Queue<Long> pendingRequests;
    private final TimeProvider timeProvider;

    /**
     * Constructor method.
     *
     * @param facultyName  - FacultyName and represents the key to the faculty.
     * @param timeProvider - TimeProvider that provides the current time. It allows for easy mocking.
     */
    public Faculty(FacultyName facultyName, TimeProvider timeProvider) {
        this.facultyName = facultyName;
        this.timeProvider = timeProvider;
        schedule = new HashMap<>();
        pendingRequests = new LinkedList<>();
    }

    /**
     * Add the request on the specified date in the schedule.
     *
     * @param request       - Request that is added to schedule.
     * @param scheduledDate - LocalDate on which the request is scheduled.
     */
    public void scheduleForDate(Request request, LocalDate scheduledDate) {
        List<Request> list = schedule.get(scheduledDate);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(request);
        //TODO: reserveResources(request, scheduledDate); //Reserve the resources for request on the scheduledDate.
        schedule.put(scheduledDate, list);
    }

    /**
     * Add a Request to pendingRequests.
     * @param request - Request that will be added.
     */
    public void addPendingRequest(Request request) {
        pendingRequests.add(request.getRequestId());
    }

    /**
     * Gets pending requests.
     *
     * @return the pending requests
     */
    public List<Long> getPendingRequests() {
        List<Long> pendingList = new ArrayList<>();
        while (!pendingRequests.isEmpty()) {
            pendingList.add(pendingRequests.remove());
        }
        return pendingList;
    }


    /**
     * Gets requests for date.
     *
     * @param date the date
     * @return the requests for date
     */
    public List<RequestDTO> getRequestsForDate(LocalDate date) {
        List<Request> ret = schedule.get(date);
        if (ret == null) {
            return new ArrayList<>();
        } else {
            return ret.stream().map(x -> new RequestDTO(
                    x.getRequestId(),
                    x.getRequestResourceManagerInformation().getName(),
                    x.getRequestFacultyInformation().getNetId(),
                    x.getRequestFacultyInformation().getFaculty(),
                    x.getRequestResourceManagerInformation().getDescription(),
                    x.getRequestFacultyInformation().getPreferredDate(),
                    x.getRequestResourceManagerInformation().getResource()))
                    .collect(Collectors.toList());
        }
    }
}

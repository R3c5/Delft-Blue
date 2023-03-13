package sem.faculty.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.commons.FacultyName;
import sem.commons.NotValidResourcesException;
import sem.commons.Resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;
    private Request request1;
    private Request request2;
    private Request request3;
    private Request request4;

    Resource resources;
    LocalDate time;

    @BeforeEach
    void setUp() throws NotValidResourcesException {
        resources = new Resource(1, 1, 1);
        time = LocalDate.of(2022, 12, 20);
        RequestDetails rd1 = new RequestDetails("Name1", "Desription1", time, RequestStatus.PENDING);
        RequestDetails rd2 = new RequestDetails("Name2", "Desription2", time, RequestStatus.DROPPED);
        RequestDetails rd3 = new RequestDetails("Name3", "Desription3", time, RequestStatus.DENIED);
        RequestDetails rd4 = new RequestDetails("Name4", "Desription4", time, RequestStatus.ACCEPTED);

        request1 = new Request(rd1, "user1", FacultyName.EEMCS, resources);

        request2 = new Request(rd2, "user1", FacultyName.EEMCS, resources);
        request3 = new Request(rd3, "user3", FacultyName.ARCH, resources);
        request4 = new Request(rd4, "user4", FacultyName.ARCH, resources);
        requestRepository.deleteAll();
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);
        requestRepository.save(request4);
    }


    @Test
    void findByNetId() {
        List<Request> toCheck = List.of(request1, request2);
        List<Request> returned = requestRepository.findByNetId("user1");
        assertThat(toCheck).isEqualTo(returned);
    }

    @Test
    void findByRequestId() {
        Request toCheck = request1;
        Request returned = requestRepository.findByRequestId(request1.getRequestId());
        assertThat(toCheck).isEqualTo(returned);
        Request toCheck2 = request2;
        Request returned2 = requestRepository.findByRequestId(request2.getRequestId());
        assertThat(toCheck2).isEqualTo(returned2);
    }

    @Test
    void findByFacultyName() {
        List<Request> toCheck = List.of(request1, request2);
        List<Request> returned = requestRepository.findByFacultyName(FacultyName.EEMCS);
        assertThat(toCheck).isEqualTo(returned);
        List<Request> toCheck2 = new ArrayList<>();
        List<Request> returned2 = requestRepository.findByFacultyName(FacultyName.AE);
        assertThat(toCheck2).isEqualTo(returned2);
    }

    @Test
    void findPendingByRequestId() {
        Request toCheck = request1;
        Request returned = requestRepository.findPendingByRequestId(request1.getRequestId());
        assertThat(toCheck).isEqualTo(returned);
        toCheck = request2;
        returned = requestRepository.findPendingByRequestId(request2.getRequestId());
        assertThat(toCheck).isNotEqualTo(returned);
    }

    @Test
    void findPendingRequests() {
        assertThat(request1.getStatus().name()).isEqualTo("PENDING");
        List<Request> toCheck = List.of(request1);
        List<Request> result = requestRepository.findPendingRequests();
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void findPendingByFacultyName() {
        assertThat(request1.getStatus().name()).isEqualTo("PENDING");
        assertThat(request1.getRequestFacultyInformation().getFaculty().name()).isEqualTo("EEMCS");
        List<Request> toCheck = List.of(request1);
        List<Request> result = requestRepository.findPendingRequestsByFaculty(FacultyName.EEMCS);
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void updateRequestStatusPending() {
        assertThat(request1.getStatus().name()).isEqualTo("PENDING");
        assertThat(request2.getStatus().name()).isEqualTo("DROPPED");
        requestRepository.updateRequestStatusPending(request1.getRequestId());
        requestRepository.updateRequestStatusPending(request2.getRequestId());
        List<Request> tocheck = new ArrayList<>();
        tocheck.add(request1);
        tocheck.add(request2);
        List<Request> result = requestRepository.findPendingRequests();
        assertThat(tocheck).isEqualTo(result);
    }


    @Test
    void findDroppedRequests() {
        assertThat(request2.getStatus().name()).isEqualTo("DROPPED");
        List<Request> toCheck = List.of(request2);
        List<Request> result = requestRepository.findDroppedRequests();
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void findDroppedByFacultyName() {
        assertThat(request2.getStatus().name()).isEqualTo("DROPPED");
        assertThat(request2.getRequestFacultyInformation().getFaculty().name()).isEqualTo("EEMCS");
        List<Request> toCheck = List.of(request2);
        List<Request> result = requestRepository.findDroppedRequestsByFaculty(FacultyName.EEMCS);
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void updateRequestStatusDropped() {
        assertThat(request1.getStatus().name()).isEqualTo("PENDING");
        assertThat(request2.getStatus().name()).isEqualTo("DROPPED");
        requestRepository.updateRequestStatusDropped(request1.getRequestId());
        requestRepository.updateRequestStatusDropped(request2.getRequestId());
        List<Request> tocheck = new ArrayList<>();
        tocheck.add(request1);
        tocheck.add(request2);
        List<Request> result = requestRepository.findDroppedRequests();
        assertThat(tocheck).isEqualTo(result);
    }

    @Test
    void findDeniedRequests() {
        assertThat(request3.getStatus().name()).isEqualTo("DENIED");
        List<Request> toCheck = List.of(request3);
        List<Request> result = requestRepository.findDeniedRequests();
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void findDeniedByFacultyName() {
        assertThat(request3.getStatus().name()).isEqualTo("DENIED");
        assertThat(request3.getRequestFacultyInformation().getFaculty().name()).isEqualTo("ARCH");
        List<Request> toCheck = List.of(request3);
        List<Request> result = requestRepository.findDeniedRequestsByFaculty(FacultyName.ARCH);
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void updateRequestStatusDenied() {
        assertThat(request1.getStatus().name()).isEqualTo("PENDING");
        assertThat(request3.getStatus().name()).isEqualTo("DENIED");
        requestRepository.updateRequestStatusDenied(request1.getRequestId());
        requestRepository.updateRequestStatusDenied(request3.getRequestId());
        List<Request> tocheck = new ArrayList<>();
        tocheck.add(request1);
        tocheck.add(request3);
        List<Request> result = requestRepository.findDeniedRequests();
        assertThat(tocheck).isEqualTo(result);
    }

    @Test
    void findAcceptedRequests() {
        assertThat(request4.getStatus().name()).isEqualTo("ACCEPTED");
        List<Request> toCheck = List.of(request4);
        List<Request> result = requestRepository.findAcceptedRequests();
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void findAcceptedByFacultyName() {
        assertThat(request4.getStatus().name()).isEqualTo("ACCEPTED");
        assertThat(request4.getRequestFacultyInformation().getFaculty().name()).isEqualTo("ARCH");
        List<Request> toCheck = List.of(request4);
        List<Request> result = requestRepository.findAcceptedRequestsByFaculty(FacultyName.ARCH);
        assertThat(toCheck).isEqualTo(result);
    }

    @Test
    void updateRequestStatusApproved() {
        assertThat(request1.getStatus().name()).isEqualTo("PENDING");
        assertThat(request4.getStatus().name()).isEqualTo("ACCEPTED");
        requestRepository.updateRequestStatusAccepted(request1.getRequestId());
        requestRepository.updateRequestStatusAccepted(request4.getRequestId());
        List<Request> tocheck = new ArrayList<>();
        tocheck.add(request1);
        tocheck.add(request4);
        List<Request> result = requestRepository.findAcceptedRequests();
        assertThat(tocheck).isEqualTo(result);
    }

}
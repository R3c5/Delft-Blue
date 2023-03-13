package sem.faculty.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sem.commons.FacultyName;
import sem.commons.NotValidResourcesException;
import sem.commons.Resource;

class RequestTest {
    Request request;
    String name;
    String netId;
    String descr;
    LocalDate date;
    Resource resource;

    @BeforeEach
    void createRequest() throws NotValidResourcesException {
        name = "Name";
        netId = "NETID";
        descr = "Description";
        date = LocalDate.of(2022, 12, 5);
        resource = new Resource(1, 1, 1);
        RequestDetails rd = new RequestDetails(name, descr, date, RequestStatus.PENDING);
        request = new Request(rd, netId, FacultyName.EEMCS, resource);
    }

    @Test
    void testEmptyConstructor() {
        Request empty = new Request();
        assertThat(empty.getRequestId()).isEqualTo(0L);
        assertThat(empty.getRequestFacultyInformation()).isNull();
        assertThat(empty.getRequestResourceManagerInformation()).isNull();
    }

    @Test
    void testConstructor() {
        assertThat(request.getRequestId()).isEqualTo(0L);
        assertThat(request.getRequestFacultyInformation().getNetId()).isEqualTo(netId);
        assertThat(request.getRequestResourceManagerInformation().getName()).isEqualTo(name);
        assertThat(request.getRequestResourceManagerInformation().getDescription()).isEqualTo(descr);
        assertThat(request.getRequestFacultyInformation().getPreferredDate()).isEqualTo(date);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(request.getRequestResourceManagerInformation().getResource()).isEqualTo(resource);
    }

    @Test
    void testToString() {
        String requestString = "Request{requestId=0, "
                + "status=PENDING, "
                + "netId='NETID', "
                + "name='Name', "
                + "description='Description', "
                + "preferredDate=2022-12-05, "
                + "faculty=EEMCS, "
                + "resource='Resource(cpu=1, gpu=1, memory=1)'"
                + "}";
        assertThat(request.toString()).isEqualTo(requestString);
    }

    @Test
    void testEquals() {
        RequestDetails rd = new RequestDetails(name, descr, date, RequestStatus.PENDING);
        Request r1 = new Request(rd, netId, FacultyName.EEMCS, resource);
        r1.setRequestId(0);
        assertThat(r1).isEqualTo(request);
        assertThat(request).isEqualTo(request);
    }

    @Test
    void testNotEquals() throws NotValidResourcesException {
        RequestDetails rd = new RequestDetails("Other", descr, date, RequestStatus.PENDING);
        Request r1 = new Request(rd, netId, FacultyName.EEMCS, resource);
        assertThat(request).isNotEqualTo(r1);

        Request r2 = new Request(rd, "Other", FacultyName.EEMCS, resource);
        assertThat(request).isNotEqualTo(r2);

        RequestDetails rd3 = new RequestDetails(name, "Other", date, RequestStatus.PENDING);
        Request r3 = new Request(rd3, netId, FacultyName.EEMCS, resource);
        assertThat(request).isNotEqualTo(r3);

        LocalDate notDate = LocalDate.of(1990, 10, 1);
        RequestDetails rd4 = new RequestDetails(name, descr, notDate, RequestStatus.PENDING);
        Request r4 = new Request(rd4, netId, FacultyName.EEMCS, resource);
        assertThat(request).isNotEqualTo(r4);

        RequestDetails rd5 = new RequestDetails(name, descr, notDate, RequestStatus.ACCEPTED);
        Request r5 = new Request(rd5, netId, FacultyName.EEMCS, resource);
        assertThat(request).isNotEqualTo(r5);

        Request r6 = new Request(rd5, netId, FacultyName.ARCH, resource);
        assertThat(request).isNotEqualTo(r6);

        Request r7 = new Request(rd5, netId, FacultyName.ARCH, new Resource(5, 1, 1));
        assertThat(request).isNotEqualTo(r7);

        Request r8 = new Request(rd5, netId, FacultyName.ARCH, resource);
        r8.setRequestId(5);
        assertThat(request).isNotEqualTo(r8);
    }

    @Test
    void testNotEqualsObject() {
        String str = "not a request";
        assertThat(request.equals(str)).isFalse();
    }

    @Test
    public void equalsHashCode() {
        RequestDetails rd = new RequestDetails(name, descr, date, RequestStatus.PENDING);
        Request r = new Request(rd, netId, FacultyName.EEMCS, resource);

        assertThat(request).isEqualTo(r);
        assertThat(r.hashCode()).isEqualTo(request.hashCode());
    }

    @Test
    void getRequestId() {
        assertThat(request.getRequestId()).isEqualTo(0L);
    }

    @Test
    void getNetId() {
        assertThat(request.getRequestFacultyInformation().getNetId()).isEqualTo(netId);
    }

    @Test
    void getName() {
        assertThat(request.getRequestResourceManagerInformation().getName()).isEqualTo(name);
    }

    @Test
    void getDescription() {
        assertThat(request.getRequestResourceManagerInformation().getDescription()).isEqualTo(descr);
    }

    @Test
    void getPreferredDate() {
        assertThat(request.getRequestFacultyInformation().getPreferredDate()).isEqualTo(date);
    }

    @Test
    void getStatus() {
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
    }

    @Test
    void getResource() {
        assertThat(request.getRequestResourceManagerInformation().getResource()).isEqualTo(resource);
    }

    @Test
    void setNetId() {
        assertThat(request.getRequestFacultyInformation().getNetId()).isEqualTo(netId);
        RequestDetails requestDetails = new RequestDetails(
                request.getRequestResourceManagerInformation().getName(),
                request.getRequestResourceManagerInformation().getDescription(),
                request.getRequestFacultyInformation().getPreferredDate(),
                request.getStatus());
        request = new Request(requestDetails, "NEW NETID",
                request.getRequestFacultyInformation().getFaculty(),
                request.getRequestResourceManagerInformation().getResource());
        assertThat(request.getRequestFacultyInformation().getNetId()).isEqualTo("NEW NETID");
    }

    @Test
    void setName() {
        assertThat(request.getRequestResourceManagerInformation().getName()).isEqualTo(name);
        RequestDetails requestDetails = new RequestDetails(
                "NEW NAME",
                request.getRequestResourceManagerInformation().getDescription(),
                request.getRequestFacultyInformation().getPreferredDate(),
                request.getStatus());
        request = new Request(requestDetails, request.getRequestFacultyInformation().getNetId(),
                request.getRequestFacultyInformation().getFaculty(),
                request.getRequestResourceManagerInformation().getResource());
        assertThat(request.getRequestResourceManagerInformation().getName()).isEqualTo("NEW NAME");
    }

    @Test
    void setDescription() {
        assertThat(request.getRequestResourceManagerInformation().getDescription()).isEqualTo(descr);
        RequestDetails requestDetails = new RequestDetails(
                request.getRequestResourceManagerInformation().getName(),
                "NEW DESCRIPTION",
                request.getRequestFacultyInformation().getPreferredDate(),
                request.getStatus());
        request = new Request(requestDetails,
                request.getRequestFacultyInformation().getNetId(),
                request.getRequestFacultyInformation().getFaculty(),
                request.getRequestResourceManagerInformation().getResource());
        assertThat(request.getRequestResourceManagerInformation().getDescription()).isEqualTo("NEW DESCRIPTION");
    }

    @Test
    void setPreferredDate() {
        assertThat(request.getRequestFacultyInformation().getPreferredDate()).isEqualTo(date);
        LocalDate newDate = LocalDate.of(2022, 12, 6);
        RequestDetails requestDetails = new RequestDetails(
                request.getRequestResourceManagerInformation().getName(),
                request.getRequestResourceManagerInformation().getDescription(),
                newDate,
                request.getStatus());
        request = new Request(requestDetails,
                request.getRequestFacultyInformation().getNetId(),
                request.getRequestFacultyInformation().getFaculty(),
                request.getRequestResourceManagerInformation().getResource());
        assertThat(request.getRequestFacultyInformation().getPreferredDate()).isEqualTo(newDate);
    }

    @Test
    void setStatus() {
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
        RequestDetails requestDetails = new RequestDetails(
                request.getRequestResourceManagerInformation().getName(),
                request.getRequestResourceManagerInformation().getDescription(),
                request.getRequestFacultyInformation().getPreferredDate(),
                RequestStatus.DENIED);
        request = new Request(requestDetails,
                request.getRequestFacultyInformation().getNetId(),
                request.getRequestFacultyInformation().getFaculty(),
                request.getRequestResourceManagerInformation().getResource());
        assertThat(request.getStatus()).isEqualTo(RequestStatus.DENIED);
    }

    @Test
    void facultyString() {
        String ret = request.facultyString();
        assertEquals("EEMCS", ret);
    }
}
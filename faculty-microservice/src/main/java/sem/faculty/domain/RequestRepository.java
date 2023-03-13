package sem.faculty.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sem.commons.FacultyName;

import java.time.LocalDateTime;
import java.util.List;



/**
 * A DDD repository for querying and persisting user aggregate roots.
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, LocalDateTime> {
    /**
     * General Queries for Requests.
     */
    @Query("SELECT r FROM Request r WHERE r.requestFacultyInformation.netId = ?1")
    List<Request> findByNetId(String netID);

    @Query("SELECT r FROM Request r WHERE r.requestId = ?1")
    Request findByRequestId(long requestID);

    @Query("SELECT r FROM Request r WHERE r.requestFacultyInformation.faculty = ?1")
    List<Request> findByFacultyName(FacultyName facultyName);

    /**
     * Queries for Pending Requests.
     */
    @Query("SELECT r FROM Request r WHERE r.status = 'PENDING' AND r.requestId = ?1")
    Request findPendingByRequestId(long requestID);

    @Query("SELECT r FROM Request r WHERE r.status = 'PENDING'")
    List<Request> findPendingRequests();

    @Query("SELECT r FROM Request r WHERE r.status = 'PENDING' AND r.requestFacultyInformation.faculty = ?1")
    List<Request> findPendingRequestsByFaculty(FacultyName facultyName);

    @Modifying
    @Query("UPDATE Request r SET r.status = 'PENDING' WHERE r.requestId = ?1")
    void updateRequestStatusPending(long requestID);

    /**
     * Queries for Dropped Requests.
     */
    @Query("SELECT r FROM Request r WHERE r.status = 'DROPPED'")
    List<Request> findDroppedRequests();

    @Query("SELECT r FROM Request r WHERE r.status = 'DROPPED' AND r.requestFacultyInformation.faculty = ?1")
    List<Request> findDroppedRequestsByFaculty(FacultyName facultyName);

    @Modifying
    @Query("UPDATE Request r SET r.status = 'DROPPED' WHERE r.requestId = ?1")
    void updateRequestStatusDropped(long requestID);

    /**
     * Queries for Denied Requests.
     */
    @Query("SELECT r FROM Request r WHERE r.status = 'DENIED'")
    List<Request> findDeniedRequests();

    @Query("SELECT r FROM Request r WHERE r.status = 'DENIED' AND r.requestFacultyInformation.faculty = ?1")
    List<Request> findDeniedRequestsByFaculty(FacultyName facultyName);

    @Modifying
    @Query("UPDATE Request r SET r.status = 'DENIED' WHERE r.requestId = ?1")
    void updateRequestStatusDenied(long requestID);

    /**
     * Queries for Accepted Requests.
     */
    @Query("SELECT r FROM Request r WHERE r.status = 'ACCEPTED'")
    List<Request> findAcceptedRequests();

    @Query("SELECT r FROM Request r WHERE r.status = 'ACCEPTED' AND r.requestFacultyInformation.faculty = ?1")
    List<Request> findAcceptedRequestsByFaculty(FacultyName facultyName);

    @Modifying
    @Query("UPDATE Request r SET r.status = 'ACCEPTED' WHERE r.requestId = ?1")
    void updateRequestStatusAccepted(long requestID);

}

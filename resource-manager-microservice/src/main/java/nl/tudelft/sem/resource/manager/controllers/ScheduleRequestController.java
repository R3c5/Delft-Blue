package nl.tudelft.sem.resource.manager.controllers;

import lombok.AllArgsConstructor;
import nl.tudelft.sem.resource.manager.domain.Resource;
import nl.tudelft.sem.resource.manager.domain.resource.Reserver;
import nl.tudelft.sem.resource.manager.domain.resource.exceptions.NotEnoughResourcesException;
import nl.tudelft.sem.resource.manager.domain.services.Manager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import sem.commons.ScheduleDateDTO;
import sem.commons.StatusDTO;

import java.time.LocalDate;

@Controller
@AllArgsConstructor
public class ScheduleRequestController {
    private final transient Manager manager;


    /**
     * Schedules a request by returning an available date.
     *
     * @param record the consumer record
     * @param scheduleDate the DTO for the request
     * @return the date on which the request can be processed
     */
    @KafkaListener(
            topics = "schedule-date",
            groupId = "default",
            containerFactory = "kafkaListenerContainerFactoryScheduleDate"
    )
    @SendTo
    public LocalDate getAvailableDateForRequest(
            ConsumerRecord<String, ScheduleDateDTO> record,
            @Payload ScheduleDateDTO scheduleDate
    ) {
        return manager.getDateForRequest(
                new Resource(
                        scheduleDate.getResources().getCpu(),
                        scheduleDate.getResources().getGpu(),
                        scheduleDate.getResources().getMemory()
                ),
                scheduleDate.getEndDate(),
                Reserver.valueOf(scheduleDate.getFacultyName().toString())
        );
    }

    /**
     * This method reserves resources for a request, and returns
     * a response regarding the success of the operation.
     *
     * @param record the consumer record
     * @param scheduleDate the DTO for the request
     * @return a {@link StatusDTO} regarding the success of the operation
     */
    @KafkaListener(
            topics = "reserve-resources",
            groupId = "default",
            containerFactory = "kafkaListenerContainerFactoryScheduleDate"
    )
    @SendTo
    public StatusDTO reserveResourcesForRequest(
            ConsumerRecord<String, ScheduleDateDTO> record,
            @Payload ScheduleDateDTO scheduleDate
    ) {
        try {
            manager.reserveResourcesOnDay(
                    Reserver.valueOf(scheduleDate.getFacultyName().toString()),
                    new Resource(
                            scheduleDate.getResources().getCpu(),
                            scheduleDate.getResources().getGpu(),
                            scheduleDate.getResources().getMemory()
                    ),
                    scheduleDate.getEndDate()
            );
        } catch (NotEnoughResourcesException e) {
            return new StatusDTO(e.toString());
        }
        return new StatusDTO("OK");
    }
}
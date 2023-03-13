package nl.tudelft.sem.template.kafka;


import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageRequest {

    private transient String message;

    public MessageRequest(@JsonProperty("message") String message) {
        this.message = message;
    }
}

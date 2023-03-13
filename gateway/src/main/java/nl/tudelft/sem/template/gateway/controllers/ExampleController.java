package nl.tudelft.sem.template.gateway.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.ExampleUser;
import sem.commons.UserCredentials;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class ExampleController {

    private final transient KafkaTemplate<String, ExampleUser> templateExampleUser;

    public ExampleController(KafkaTemplate<String, ExampleUser> templateExampleUser) {
        this.templateExampleUser = templateExampleUser;
    }

    @PostMapping("example/user")
    public void sendToTopic(@RequestBody ExampleUser user) {
        System.out.println(user.getNetId());
        templateExampleUser.send("example-topic", user);
    }

    @PostMapping("user/login")
    public void authenticateUser(@RequestBody UserCredentials userCredentials) {
        System.out.println("Tried authenticating user with netId: " + userCredentials.getNetId());
    }

}

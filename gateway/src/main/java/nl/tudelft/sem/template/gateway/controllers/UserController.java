package nl.tudelft.sem.template.gateway.controllers;

import nl.tudelft.sem.template.gateway.authentication.AuthManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sem.commons.StatusDTO;
import sem.commons.TokenDTO;
import sem.commons.UserCredentials;
import sem.commons.UserDTO;

import java.util.concurrent.ExecutionException;

@RestController
public class UserController {

    private final transient AuthManager authManager;
    private transient ReplyingKafkaTemplate<String, UserDTO, StatusDTO> replyingKafkaTemplateNewUser;
    private transient ReplyingKafkaTemplate<String, UserCredentials, TokenDTO> replyingKafkaTemplateUserAuth;

    /**
     * Instantiates a new User controller.
     *
     * @param authManager                   the auth manager
     * @param replyingKafkaTemplateNewUser  the replying kafka template new user
     * @param replyingKafkaTemplateUserAuth the replying kafka template user auth
     */
    @Autowired
    public UserController(
            AuthManager authManager,
            ReplyingKafkaTemplate<String, UserDTO, StatusDTO> replyingKafkaTemplateNewUser,
            ReplyingKafkaTemplate<String, UserCredentials, TokenDTO> replyingKafkaTemplateUserAuth
    ) {
        this.authManager = authManager;
        this.replyingKafkaTemplateNewUser = replyingKafkaTemplateNewUser;
        this.replyingKafkaTemplateUserAuth = replyingKafkaTemplateUserAuth;
    }

    /**
     * Add new user response entity.
     *
     * @param userDTO the user dto
     * @return the response entity
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    @PostMapping("/user/add")
    public ResponseEntity<String> addNewUser(@RequestBody UserDTO userDTO) throws ExecutionException, InterruptedException {

        ProducerRecord<String, UserDTO> record = new ProducerRecord<>("add-user-topic", userDTO);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "add-user-topic-reply".getBytes()));

        RequestReplyFuture<String, UserDTO, StatusDTO> future =
                replyingKafkaTemplateNewUser.sendAndReceive(record);

        ConsumerRecord<String, StatusDTO> consumerRecord = future.get();

        StatusDTO statusDTO = consumerRecord.value();

        if (statusDTO.getStatus().equals("OK")) {
            return ResponseEntity.ok("Added new user");
        } else {
            return ResponseEntity.status(400).body(statusDTO.getStatus());
        }
    }

    /**
     * Authenticate user response entity.
     *
     * @param userCredentials the user credentials
     * @return the response entity
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    @PostMapping("/user/authenticate")
    public ResponseEntity<TokenDTO> authenticateUser(@RequestBody UserCredentials userCredentials)
            throws ExecutionException, InterruptedException {

        ProducerRecord<String, UserCredentials> record = new ProducerRecord<>("user-auth-topic", userCredentials);

        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "user-auth-topic-reply".getBytes()));

        RequestReplyFuture<String, UserCredentials, TokenDTO> future =
                replyingKafkaTemplateUserAuth.sendAndReceive(record);

        ConsumerRecord<String, TokenDTO> consumerRecord = future.get();

        TokenDTO tokenDTO = consumerRecord.value();

        if (tokenDTO.getStatus().equals("OK")) {
            return ResponseEntity.ok(tokenDTO);
        } else {
            return ResponseEntity.status(401).body(tokenDTO);
        }
    }


    /**
     * Hello world response entity.
     *
     * @return the response entity
     */
    @GetMapping("/user/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok(
                "Hello " + authManager.getNetId() + " from faculty " + authManager.getFaculties()
        );
    }
}

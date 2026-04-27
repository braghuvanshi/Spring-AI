package com.llm.chats;


import com.llm.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("v1/chats")
    public Object chat(@RequestBody UserInput userInput) {
        log.info("UserInput message : {}", userInput);
        var requestSpec = chatClient
                .prompt()
                .user(userInput.prompt());

        log.info("requestSpec : {}", requestSpec);
        var responseSpec = requestSpec.call();
        log.info("responseSpec : {}", responseSpec);
        log.info("content : {}", responseSpec.content());

        return responseSpec.content();
    }

    @PostMapping("v2/chats")
    public Object chatV2(@RequestBody UserInput userInput) {
        log.info("UserInput message : {}", userInput);

        var systemMessage = """
                You are a helpful assistant, who can answer java based questions.
                For any other questions, please respond with I don't know in a funny way!
                """;

        var requestSpec = chatClient
                .prompt()
                .user(userInput.prompt())
                .system(systemMessage);

        log.info("requestSpec : {}", requestSpec);
        var responseSpec = requestSpec.call();
        log.info("responseSpec : {}", responseSpec);
        log.info("content : {}", responseSpec.content());

        return responseSpec.content();
    }

    @PostMapping("v1/chats/stream")
    public Flux<String> chatWithStream(@RequestBody UserInput userInput) {
       return chatClient
               .prompt()
               .user(userInput.prompt())
               .stream()
               .content()
               .doOnNext( s -> log.info("Streamed content : {}", s))
               .doOnComplete(() -> log.info("Stream completed!"))
               //.onErrorReturn("An error occurred while streaming the response.");
               .onErrorResume(e -> {
                   log.error("Error during streaming:  {}", e.getMessage());
                   //return Flux.just("An error occurred while streaming the response." + e.getMessage());
                   return Flux.error(new RuntimeException("An error occurred while streaming the response." + e.getMessage()));
               });
    }

}

package com.llm.chats;

import com.llm.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PromptController {


    private static final Logger log = LoggerFactory.getLogger(PromptController.class);
    private final ChatClient chatClient;
    @Value("classpath:/prompt-templates/java-coding-assistant.st")
    private Resource systemTemplateMessage;

    @Value("classpath:/prompt-templates/coding-assistant.st")
    private Resource systemText;


    public PromptController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/v1/prompts")
    public String prompts(@RequestBody UserInput userInput){
        log.info("userInput : {} ", userInput);
        var systemMessage = """
                You are a helpful assistant, who can answer java based questions.
                For any other questions, please respond with I don't know in a funny way!
                """;
        var sysMessage = new SystemMessage(systemTemplateMessage);
        var userMessage = new UserMessage(userInput.prompt());

        var promptMessage = new Prompt(List.of(sysMessage,
//                new UserMessage("Whats My name ?"),
//                new AssistantMessage("I don't know!"),
//                new UserMessage("My name is Dilip"),
                userMessage));

        var responseSpec = chatClient.prompt(promptMessage).call();
        return responseSpec.content();

    }




}

package com.llm.prompt_engineering;

import com.llm.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TravelAssistantController {

    private static final Logger log = LoggerFactory.getLogger(TravelAssistantController.class);
    private final ChatClient chatClient;

    @Value("classpath:/prompt-templates/travel_prompt.st")
    private Resource travelPromptMessage;

    public TravelAssistantController(OpenAiChatModel openAiChatModel) {
        this.chatClient = ChatClient.builder(openAiChatModel).build();
    }


    @PostMapping("/v1/travel_assistant")
    public String prompts(@RequestBody UserInput userInput) {
        log.info("userInput : {} ", userInput);

        return chatClient.prompt()
                .user(userInput.prompt())
                .call()
                .content();
    }

    @PostMapping("/v2/travel_assistant")
    public String promptsv2(@RequestBody UserInput userInput) {
        log.info("userInput : {} ", userInput);

        var systemMessage = """
                You are a professional travel planner with extensive knowledge of worldwide destinations,
                including cultural attractions, accommodations, and travel logistics.
                Provide better lodging options too that supports the family.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(travelPromptMessage);
        String userPrompt = promptTemplate.render(Map.of("context", userInput.context(), "input", userInput.prompt()));

        return chatClient.prompt()
                .system(systemMessage)
                .user(userPrompt)
                .call()
                .content();
    }

}

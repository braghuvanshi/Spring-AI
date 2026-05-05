package com.llm.prompt_engineering;

import com.llm.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PromptTypesController {

    private static final Logger log = LoggerFactory.getLogger(TravelAssistantController.class);
    private final ChatClient chatClient;

    public PromptTypesController(OpenAiChatModel openAiChatModel) {
        this.chatClient = ChatClient.builder(openAiChatModel).build();
    }


    @Value("classpath:/prompt-templates/prompt_types/few_shot.st")
    private Resource fewShotPrompt;

    @Value("classpath:/prompt-templates/prompt_types/multi_step_prompt_1.st")
    private Resource multiStep1;

    @Value("classpath:/prompt-templates/prompt_types/multi_step_prompt_2.st")
    private Resource multiStep2;


    @PostMapping("/v1/prompt_types/zero_shot")
    public String zeroShot(@RequestBody UserInput userInput) {
        log.info("userInput : {} ", userInput);

        return chatClient.prompt()
                .user(userInput.prompt())
                .call()
                .content();
    }

    //   happy
    // unhappy

    @PostMapping("/v1/prompt_types/few_shot")
    public String fewShot(@RequestBody UserInput userInput) {
        log.info("userInput : {} ", userInput);

        var fewShotExamples = """
                Prompt : "The product arrived quickly, worked perfectly, and exceeded my expectations!"
                Answer : happy
                
                Prompt : "Great quality, fast shipping, and exactly as described—highly recommend!"
                Answer : happy
                
                Prompt : "The item arrived broken and didn’t function at all—very disappointing!"
                Answer : unhappy
                
                Prompt : "Poor packaging led to a damaged product that was completely useless."
                Answer : unhappy
                
                """;

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(fewShotPrompt);
        String systemPrompt = systemPromptTemplate.render(Map.of("few_shot_prompts", fewShotExamples));

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userInput.prompt())
                .call()
                .content();
    }

    @PostMapping("/v1/prompt_types/cot")
    public String cot(@RequestBody UserInput userInput) {
        log.info("userInput : {} ", userInput);

        return chatClient.prompt()
                .user(userInput.prompt())
                .call()
                .content();
    }

    @PostMapping("/v1/prompt_types/multi_step")
    public String multistep_1(@RequestBody UserInput userInput) {
        log.info("userInput : {} ", userInput);
//        PromptTemplate promptTemplate = new PromptTemplate(multiStep1);
        PromptTemplate promptTemplate = new PromptTemplate(multiStep2);
        String userPrompt = promptTemplate.render(Map.of("input", userInput.prompt()));
        log.info("prompt : {} ", userPrompt);

        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }
}

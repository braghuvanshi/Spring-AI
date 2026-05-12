package com.llm.structuredoutputs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.llm.dto.flight.FlightBooking;
import com.llm.dto.UserInput;
import com.llm.dto.soccer.SoccerTeam;
import com.llm.utils.CommonUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class StructuredOutputsController {

    private static final Logger log = LoggerFactory.getLogger(StructuredOutputsController.class);

    private final ChatClient chatClient;

    private final ObjectMapper objectMapper;

    public StructuredOutputsController(OpenAiChatModel openAiChatModel, ObjectMapper objectMapper) {
        this.chatClient = ChatClient.builder(openAiChatModel).build();
        this.objectMapper = objectMapper;
    }

    @Value("classpath:/prompt-templates/structured_outputs/flight_details.st")
    private Resource flightBooking;

    @Value("classpath:/prompt-templates/structured_outputs/flight_details_fewshot.st")
    private Resource flightBookingFewShot;



    @PostMapping("/v1/structured_outputs")
    public String structuredOutputs(@RequestBody @Valid UserInput userInput) {

        log.info("userInput message : {} ", userInput);

        return chatClient.prompt()
                .user(userInput.prompt())
                .call()
                .content();
    }

    @PostMapping("/v1/structured_outputs/fewshot")
    public String structuredOutputsFewShot(@RequestBody @Valid UserInput userInput) {

        log.info("userInput message : {} ", userInput);

        var promptTemplate = new PromptTemplate(flightBookingFewShot);
        String userPrompt = promptTemplate.render(Map.of("input", userInput.prompt(),
                "jsonexample" , CommonUtils.flightJson()));

        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    @PostMapping("/v1/structured_outputs/entity")
    public FlightBooking entity(@RequestBody @Valid UserInput userInput) {

        log.info("userInput message : {} ", userInput);

        var promptTemplate = new PromptTemplate(flightBooking);
        String userPrompt = promptTemplate.render(Map.of("input", userInput.prompt()));

        var booking = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .entity(FlightBooking.class);

        log.info("booking : {} ", booking);
        return booking;
    }

    @PostMapping("/v1/structured_outputs/entity/list")
    public List<String> entityList(@RequestBody @Valid UserInput userInput) {

        log.info("userInput message : {} ", userInput);

        var soccerTeams = chatClient
                .prompt()
                .user(userInput.prompt())
                .call()
                //.entity(new ParameterizedTypeReference<List<SoccerTeam>>() {});
                .entity(new ListOutputConverter(new DefaultConversionService()));


        log.info("soccerTeams : {} ", soccerTeams);
        return soccerTeams;
    }

    @PostMapping("/v1/structured_outputs/entity/map")
    public Map<String, Object> entityMap(@RequestBody @Valid UserInput userInput) {

        log.info("userInput message : {} ", userInput);

        MapOutputConverter mapOutputConverter = new MapOutputConverter();

        String format = mapOutputConverter.getFormat();
        String template = """
        Input : {input}
        {format}
        """;
        var promptTemplate = new PromptTemplate(template);
        String userPrompt = promptTemplate.render(
                Map.of("input", userInput.prompt(), "format", format));

        log.info("userPrompt : {} ", userPrompt);

        var soccerTeamsByMap = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();


        log.info("soccerTeamsByMap : {} ", soccerTeamsByMap);

        var result = mapOutputConverter.convert(soccerTeamsByMap);
        return result;
    }
}
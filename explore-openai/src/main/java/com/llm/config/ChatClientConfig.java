package com.llm.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

//    @Bean
//    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
//        return ChatClient.builder(openAiChatModel).build();
//    }
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatModel chatModel) {
        var advisor = ChatModelCallAdvisor.builder()
                .chatModel(chatModel)
                .build();     return chatClientBuilder
                .defaultAdvisors(advisor)
                .build();
    }
}


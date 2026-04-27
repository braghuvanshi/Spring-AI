# Fix for "No CallAdvisors available to execute" Error

## Problem
The error "No CallAdvisors available to execute" was occurring when calling OpenAI API endpoints because the `ChatClient.Builder` was being used without configuring the underlying chat model.

## Root Cause
When creating a `ChatClient` from `ChatClient.Builder`, the builder needs to have access to the actual chat model (like `OpenAiChatModel`) to execute the API calls. The advisor chain requires a model at the end of the chain to make the actual call to OpenAI.

## Solution Applied
Changed all controllers to use `ChatClient.builder(openAiChatModel)` instead of injecting `ChatClient.Builder` without a model.

### Files Modified

1. **ChatController.java**
   - Changed constructor from `ChatClient.Builder chatClientBuilder` to `OpenAiChatModel openAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

2. **AdvisorsChatController.java**
   - Changed constructor from `ChatClient.Builder chatClientBuilder` to `OpenAiChatModel openAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

3. **PromptController.java**
   - Changed constructor from `ChatClient.Builder chatClientBuilder` to `OpenAiChatModel openAiChatModel`
   - Added import for `OpenAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

4. **StreamController.java**
   - Changed constructor from `ChatClient.Builder builder` to `OpenAiChatModel openAiChatModel`
   - Added import for `OpenAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

5. **TravelAssistantController.java**
   - Changed constructor from `ChatClient.Builder chatClientBuilder` to `OpenAiChatModel openAiChatModel`
   - Added import for `OpenAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

6. **PromptTypesController.java**
   - Changed constructor from `ChatClient.Builder chatClientBuilder` to `OpenAiChatModel openAiChatModel`
   - Added import for `OpenAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

7. **PromptInjectionController.java**
   - Changed constructor from `ChatClient.Builder chatClientBuilder` to `OpenAiChatModel openAiChatModel`
   - Added import for `OpenAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

8. **StructuredOutputsController.java**
   - Changed constructor from `ChatClient.Builder chatClientBuilder` to `OpenAiChatModel openAiChatModel`
   - Added import for `OpenAiChatModel`
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

9. **ToolCallingController.java**
   - Changed constructor to remove `ChatClient.Builder builder` parameter
   - Updated to use `ChatClient.builder(openAiChatModel).build()`

## How to Test
1. Restart your Spring Boot application
2. Call any of the OpenAI endpoints (e.g., `/springai/v1/chats`, `/springai/v1/advisors`)
3. The "No CallAdvisors available to execute" error should no longer appear
4. You should receive proper responses from the OpenAI API

## Pattern to Follow for Future Controllers
When creating new controllers that use ChatClient, always inject the model directly:

```java
@RestController
public class MyController {
    private final ChatClient chatClient;

    public MyController(OpenAiChatModel openAiChatModel) {
        this.chatClient = ChatClient.create(openAiChatModel);
    }
}
```

Do NOT use:
```java
// ❌ INCORRECT - Will cause "No CallAdvisors available to execute" error
public MyController(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
}
```


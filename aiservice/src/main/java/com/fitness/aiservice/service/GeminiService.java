package com.fitness.aiservice.service;


import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GoogleGenAiChatModel chatModel;

    public GeminiService(GoogleGenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getAnswer(String question) {
        String response = chatModel.call(question);
        return response;
    }
}

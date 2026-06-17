package com.knobull.searchengine.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();

    public float[] embed(String text) {
        String url = "https://api-inference.huggingface.co/models/sentence-transformers/all-MiniLM-L6-v2";

        restTemplate.postForObject(url, text, String.class);

        // Simplified stub — parse the Hugging Face JSON response in production.
        return new float[384];
    }
}
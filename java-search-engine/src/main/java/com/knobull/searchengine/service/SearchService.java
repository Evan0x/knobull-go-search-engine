package com.knobull.searchengine.service;

import com.knobull.searchengine.model.Resource;
import com.knobull.searchengine.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final ResourceRepository repository;
    private final EmbeddingService embeddingService;

    public SearchService(ResourceRepository repository,
                         EmbeddingService embeddingService) {
        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    public List<Resource> search(String query) {

        float[] embedding = embeddingService.embed(query);

        return repository.searchByEmbedding(embedding);
    }
}
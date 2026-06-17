package com.knobull.searchengine.controller;

import com.knobull.searchengine.model.Resource;
import com.knobull.searchengine.model.SearchRequest;
import com.knobull.searchengine.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    public List<Resource> search(@RequestBody SearchRequest request) {
        return searchService.search(request.query);
    }
}
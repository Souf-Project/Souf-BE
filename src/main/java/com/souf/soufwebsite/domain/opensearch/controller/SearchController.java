package com.souf.soufwebsite.domain.opensearch.controller;

import com.souf.soufwebsite.domain.opensearch.dto.SearchResDto;
import com.souf.soufwebsite.domain.opensearch.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public List<SearchResDto> searchAll(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws IOException {
        log.info("SearchController: searchAll called with keyword: {}", keyword);
        return searchService.searchAll(keyword, page, size);
    }
}
//package com.souf.soufwebsite.domain.opensearch.controller;
//
//import com.souf.soufwebsite.domain.opensearch.service.InitialIndexingService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/admin")
//public class IndexAdminController {
//
//    private final InitialIndexingService initialIndexingService;
//
//    @PostMapping("/bulk-reindex")
//    public String bulkReindexAll() throws IOException {
//        initialIndexingService.bulkIndexAll();
//        return "✅ DB 데이터 전체 bulk 색인 완료!";
//    }
//}
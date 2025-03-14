package com.souf.soufwebsite.domain.recruit.controller;

import com.souf.soufwebsite.domain.recruit.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recruit")
public class RecruitController {

    private final RecruitService recruitService;


}

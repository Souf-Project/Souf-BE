package com.souf.soufwebsite.global.common.viewCount.controller;

import com.souf.soufwebsite.global.common.viewCount.dto.ViewCountResDto;
import com.souf.soufwebsite.global.common.viewCount.service.ViewCountService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.souf.soufwebsite.global.common.viewCount.controller.ViewControllerSuccessMessage.GET_COUNT_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/view")
public class ViewController implements ViewApiSpecification{

    private final ViewCountService viewCountService;

    @GetMapping("/main")
    public SuccessResponse<ViewCountResDto> getViewCountFromMain() {

        log.info("메인 페이지 방문자 수 캐싱 중");

        return new SuccessResponse<>(viewCountService.getViewCountFromMain(), GET_COUNT_SUCCESS.getMsg());
    }
}

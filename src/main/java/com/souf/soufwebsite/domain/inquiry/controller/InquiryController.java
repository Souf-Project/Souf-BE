package com.souf.soufwebsite.domain.inquiry.controller;

import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.inquiry.service.InquiryService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.inquiry.controller.InquirySuccessMessage.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public SuccessResponse<?> create(
            @CurrentEmail String email,
            @RequestBody InquiryReqDto reqDto) {

        inquiryService.createInquiry(email, reqDto);

        return new SuccessResponse<>(INQUIRY_CREATE.getMessage());
    }

    @PatchMapping("/{inquiryId}")
    public SuccessResponse<?> updateInquiry(
            @CurrentEmail String email,
            @RequestBody InquiryReqDto reqDto,
            @PathVariable(name = "inquiryId") Long inquiryId
    ){
      inquiryService.updateInquiry(email, inquiryId, reqDto);
      return new SuccessResponse<>(INQUIRY_UPDATE.getMessage());
    }

    @DeleteMapping("/{inquiryId}")
    public SuccessResponse<?> deleteInquiry(
            @CurrentEmail String email,
            @PathVariable(name = "inquiryId") Long inquiryId
    ) {
        inquiryService.deleteInquiry(email, inquiryId);

        return new SuccessResponse<>(INQUIRY_DELETE.getMessage());
    }
}

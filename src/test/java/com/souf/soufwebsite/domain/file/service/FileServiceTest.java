package com.souf.soufwebsite.domain.file.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @DisplayName("제공될 사진에 대해 presignedUrl을 발행합니다.")
    @Test
    void postPresignedUrlWithName(){
        String presignedUploadUrl = fileService.generatePresingedUploadUrl("originalFilename.jpg");

        System.out.println(presignedUploadUrl);
        assertThat(presignedUploadUrl).isNotNull();
    }
}
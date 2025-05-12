package com.souf.soufwebsite.domain.file.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @DisplayName("file에 대한 presigned url을 생성합니다.")
    @Test
    void postPresignedUrl(){
        String fileName = fileService.generatePresingedUploadUrl("originalFileName");
        System.out.println(fileName);

        assertThat(fileName).isNotNull();
    }
}
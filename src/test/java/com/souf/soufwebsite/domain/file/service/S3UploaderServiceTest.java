package com.souf.soufwebsite.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3UploaderServiceTest {

    @InjectMocks
    private S3UploaderService s3UploaderService;

    @Mock
    private AmazonS3 amazonS3;

    @DisplayName("file에 대한 presigned url을 생성합니다.")
    @Test
    void postPresignedUrl() throws Exception {
        //given
        URL presignedUrl = new URL("https://s3.amazonaws.com/test-bucket/originalFileName?AWSAccessKeyId=...");

        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(presignedUrl);

        //when
        PresignedUrlResDto fileName = s3UploaderService.generatePresignedUploadUrl("recruit", "originalFileName");
        System.out.println(fileName);


        //then
        assertThat(fileName).isNotNull();
    }
}
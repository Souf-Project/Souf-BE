package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FeedServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedService feedService;

    @Test
    void createFeed_성공() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",                         // DTO에서의 field명
                "image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/feeds")
                        .file(file)
                        .param("content", "테스트 피드입니다.")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Feed created successfully"));
    }

//    @Test
//    void getFeeds() {
//        // given
//        User testUser = new User(1L, "testUser", "nickname", "test@example.com");
//
//        List<Feed> feeds = List.of(
//                Feed.of("content1", null),
//                Feed.of("content2", null)
//        );
//
//        // when
//        List<FeedResDto> feedResDtos = feeds.stream()
//                .map(feed -> new FeedResDto(feed.getId(), feed.getContent(), List.of(), "nickname"))
//                .toList();
//
//        // then
//        assertThat(feedResDtos).hasSize(2);
//    }

}
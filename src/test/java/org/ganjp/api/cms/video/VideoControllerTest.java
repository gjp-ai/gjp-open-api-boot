package org.ganjp.api.cms.video;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private VideoService videoService;

        @Test
        void should_returnPaginatedVideos_when_getVideos() throws Exception {
                VideoResponse videoResponse = VideoResponse.builder()
                                .id("abc-123")
                                .title("Controller Test")
                                .build();

                PaginatedResponse<VideoResponse> paginatedData = PaginatedResponse.of(
                                List.of(videoResponse), 0, 20, 1);

                when(videoService.getVideos(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyString(),
                                anyString()))
                                .thenReturn(paginatedData);

                mockMvc.perform(get("/open/videos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void should_returnBadRequest_when_invalidLanguage() throws Exception {
                mockMvc.perform(get("/open/videos?lang=INVALID_LANG"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(400))
                                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
        }

        @Test
        void should_returnVideoDetail_when_foundById() throws Exception {
                VideoResponse detailResponse = VideoResponse.builder()
                                .id("def-456")
                                .title("Detail View")
                                .build();

                when(videoService.getVideoById("def-456")).thenReturn(detailResponse);

                mockMvc.perform(get("/open/videos/def-456"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.id").value("def-456"))
                                .andExpect(jsonPath("$.data.title").value("Detail View"));
        }

        @Test
        void should_returnNotFound_when_videoIdDoesNotExist() throws Exception {
                when(videoService.getVideoById("not-found")).thenReturn(null);

                mockMvc.perform(get("/open/videos/not-found"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(404))
                                .andExpect(jsonPath("$.status.message").value("Video not found"));
        }
}

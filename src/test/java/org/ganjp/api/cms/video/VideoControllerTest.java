package org.ganjp.api.cms.video;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

        @Test
        void should_streamFullVideo_when_noRangeHeader() throws Exception {
                File videoFile = Files.createTempFile("video-full", ".mp4").toFile();
                Files.write(videoFile.toPath(), "0123456789".getBytes());
                when(videoService.getVideoFileByFilename("sample.mp4")).thenReturn(videoFile);

                mockMvc.perform(get("/open/videos/view/sample.mp4"))
                                .andExpect(status().isOk())
                                .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, "bytes"))
                                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"sample.mp4\""))
                                .andExpect(content().bytes("0123456789".getBytes()));
        }

        @Test
        void should_streamVideoRange_when_rangeHeaderPresent() throws Exception {
                File videoFile = Files.createTempFile("video-range", ".mp4").toFile();
                Files.write(videoFile.toPath(), "0123456789".getBytes());
                when(videoService.getVideoFileByFilename("sample.mp4")).thenReturn(videoFile);

                mockMvc.perform(get("/open/videos/view/sample.mp4")
                                .header(HttpHeaders.RANGE, "bytes=2-5"))
                                .andExpect(status().isPartialContent())
                                .andExpect(header().string(HttpHeaders.ACCEPT_RANGES, "bytes"))
                                .andExpect(header().string(HttpHeaders.CONTENT_RANGE, "bytes 2-5/10"))
                                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, 4))
                                .andExpect(content().bytes("2345".getBytes()));
        }

        @Test
        void should_returnCoverImage_when_videoCoverExists() throws Exception {
                File coverFile = Files.createTempFile("video-cover", ".png").toFile();
                Files.write(coverFile.toPath(), "png-data".getBytes());
                when(videoService.getCoverImageFileByFilename("cover.png")).thenReturn(coverFile);

                mockMvc.perform(get("/open/videos/cover-images/cover.png"))
                                .andExpect(status().isOk())
                                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                                                "inline; filename=\"cover.png\""))
                                .andExpect(content().bytes("png-data".getBytes()));
        }
}

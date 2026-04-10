package org.ganjp.api.cms.website;

import org.ganjp.api.core.model.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebsiteController.class)
class WebsiteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private WebsiteService websiteService;

        @Test
        void should_returnPaginatedWebsites_when_getWebsites() throws Exception {
                // Given
                WebsiteResponse websiteResponse = WebsiteResponse.builder()
                                .id("abc-123")
                                .name("Website Test")
                                .build();

                PaginatedResponse<WebsiteResponse> paginatedData = PaginatedResponse.of(
                                List.of(websiteResponse), 0, 20, 1);

                when(websiteService.getWebsites(isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyString(),
                                anyString()))
                                .thenReturn(paginatedData);

                // When & Then
                mockMvc.perform(get("/open/websites"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.content[0].id").value("abc-123"))
                                .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        void should_returnBadRequest_when_invalidLanguage() throws Exception {
                // When & Then
                mockMvc.perform(get("/open/websites?lang=INVALID_LANG"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(400))
                                .andExpect(jsonPath("$.status.message").value("Invalid lang"));
        }

        @Test
        void should_returnWebsiteDetail_when_foundById() throws Exception {
                // Given
                WebsiteResponse detailResponse = WebsiteResponse.builder()
                                .id("def-456")
                                .name("Detail View")
                                .build();

                when(websiteService.getWebsiteById("def-456")).thenReturn(detailResponse);

                // When & Then
                mockMvc.perform(get("/open/websites/def-456"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(200))
                                .andExpect(jsonPath("$.data.id").value("def-456"))
                                .andExpect(jsonPath("$.data.name").value("Detail View"));
        }

        @Test
        void should_returnNotFound_when_websiteIdDoesNotExist() throws Exception {
                // Given
                when(websiteService.getWebsiteById("not-found")).thenReturn(null);

                // When & Then
                mockMvc.perform(get("/open/websites/not-found"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status.code").value(404))
                                .andExpect(jsonPath("$.status.message").value("Website not found"));
        }
}

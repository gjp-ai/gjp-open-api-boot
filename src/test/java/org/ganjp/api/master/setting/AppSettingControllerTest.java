package org.ganjp.api.master.setting;

import org.ganjp.api.core.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppSettingController.class)
@Import(GlobalExceptionHandler.class)
class AppSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppSettingService appSettingService;

    @Test
    void should_returnAppSettings_when_getAllAppSettings() throws Exception {
        // Given
        AppSettingDto dto = new AppSettingDto();
        dto.setName("test");
        dto.setValue("val");
        when(appSettingService.getAllAppSettings()).thenReturn(List.of(dto));

        // When & Then
        mockMvc.perform(get("/open/app-settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("test"))
                .andExpect(jsonPath("$.status.message").value("Public app settings retrieved successfully"));
    }
}

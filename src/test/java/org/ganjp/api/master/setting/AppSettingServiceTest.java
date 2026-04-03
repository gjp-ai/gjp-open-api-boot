package org.ganjp.api.master.setting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppSettingServiceTest {

    @Mock
    private AppSettingRepository appSettingRepository;

    @InjectMocks
    private AppSettingService appSettingService;

    @Test
    void should_returnPublicSettings_when_getAllAppSettings() {
        // Given
        AppSetting setting = AppSetting.builder()
                .id("1")
                .name("site_name")
                .value("GJP")
                .lang(AppSetting.Language.EN)
                .isPublic(true)
                .build();
        when(appSettingRepository.findByIsPublicTrueOrderByNameAscLangAsc()).thenReturn(List.of(setting));

        // When
        List<AppSettingDto> result = appSettingService.getAllAppSettings();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("site_name");
        assertThat(result.get(0).getValue()).isEqualTo("GJP");
    }
}

package org.ganjp.api.master.setting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AppSettingService {
    private final AppSettingRepository appSettingRepository;

    public List<AppSettingDto> getAllAppSettings(String channel) {
        List<AppSetting> settings = appSettingRepository.findByIsPublicTrueAndChannelOrderByNameAscLangAsc(channel);
        return settings.stream().map(AppSettingDto::fromEntity).toList();
    }
}

package org.ganjp.api;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class GjpApiPublicApplicationMainTest {

    @Test
    void should_delegateToSpringApplication_when_mainRuns() {
        String[] args = { "--spring.profiles.active=test" };

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            GjpApiPublicApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(GjpApiPublicApplication.class, args));
        }
    }
}

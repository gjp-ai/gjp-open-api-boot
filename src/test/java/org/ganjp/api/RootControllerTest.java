package org.ganjp.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RootControllerTest {

    @Test
    void should_returnWelcomeMessage_when_getWelcomeMessage() {
        RootController controller = new RootController();

        String message = controller.getWelcomeMessage();

        assertThat(message).isEqualTo("Welcome to Gan Jian Ping Public API");
    }
}

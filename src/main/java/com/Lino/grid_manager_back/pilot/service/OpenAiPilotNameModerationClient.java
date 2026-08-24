package com.Lino.grid_manager_back.pilot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "grid-manager.moderation.openai-enabled", havingValue = "true")
public class OpenAiPilotNameModerationClient implements PilotNameModerationClient {
    private final RestClient restClient;

    public OpenAiPilotNameModerationClient(
            RestClient.Builder restClientBuilder,
            @Value("${GRID_MANAGER_OPENAI_API_KEY}") String apiKey) {
        this.restClient = restClientBuilder.baseUrl("https://api.openai.com/v1").defaultHeader("Authorization", "Bearer " + apiKey).build();
    }

    @Override
    public boolean isFlagged(String value) {
        ModerationResponse response = restClient.post().uri("/moderations").contentType(MediaType.APPLICATION_JSON)
                .body(new ModerationRequest("omni-moderation-latest", value)).retrieve().body(ModerationResponse.class);
        return response != null && !response.results().isEmpty() && response.results().getFirst().flagged();
    }

    private record ModerationRequest(String model, String input) {}
    private record ModerationResponse(List<ModerationResult> results) {}
    private record ModerationResult(boolean flagged) {}
}

package ar.edu.unsam.ddso.tp.bot;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "telegram.bot.token")
public class TelegramClient {

    private static final String API_URL = "https://api.telegram.org/bot";

    private final RestClient restClient;

    public TelegramClient(@Value("${telegram.bot.token}") String token) {
        this.restClient = RestClient.builder().baseUrl(API_URL + token.trim()).build();
    }

    public List<TelegramUpdate> getUpdates(long offset, int timeoutSeconds) {
        TelegramUpdatesResponse response =
                restClient
                        .get()
                        .uri("/getUpdates?offset={offset}&timeout={timeout}", offset, timeoutSeconds)
                        .retrieve()
                        .body(TelegramUpdatesResponse.class);
        if (response == null || !response.ok() || response.result() == null) {
            return List.of();
        }
        return response.result();
    }

    public void sendMessage(long chatId, String text) {
        restClient
                .post()
                .uri("/sendMessage")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Map.of(
                                "chat_id", chatId,
                                "text", text,
                                "link_preview_options", Map.of("is_disabled", true)))
                .retrieve()
                .toBodilessEntity();
    }
}

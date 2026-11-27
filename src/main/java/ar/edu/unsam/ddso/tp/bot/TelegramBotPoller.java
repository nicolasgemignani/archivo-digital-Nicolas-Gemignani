package ar.edu.unsam.ddso.tp.bot;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "telegram.bot.token")
public class TelegramBotPoller {

    private static final Logger LOG = LoggerFactory.getLogger(TelegramBotPoller.class);
    private static final int LONG_POLLING_TIMEOUT_SECONDS = 25;

    private final TelegramClient telegramClient;
    private final TelegramBotHandler botHandler;
    private long nextUpdateId;

    public TelegramBotPoller(TelegramClient telegramClient, TelegramBotHandler botHandler) {
        this.telegramClient = telegramClient;
        this.botHandler = botHandler;
    }

    @Scheduled(fixedDelay = 1000)
    public void poll() {
        List<TelegramUpdate> updates;
        try {
            updates = telegramClient.getUpdates(nextUpdateId, LONG_POLLING_TIMEOUT_SECONDS);
        } catch (RuntimeException ex) {
            LOG.warn("Could not get updates from Telegram: {}", ex.getMessage());
            return;
        }
        for (TelegramUpdate update : updates) {
            nextUpdateId = update.updateId() + 1;
            process(update);
        }
    }

    private void process(TelegramUpdate update) {
        TelegramMessage message = update.message();
        if (message == null || message.text() == null || message.chat() == null) {
            return;
        }
        String reply;
        try {
            reply = botHandler.handle(message.text());
        } catch (RuntimeException ex) {
            LOG.error("Error handling Telegram message", ex);
            reply = TelegramBotHandler.ERROR_MESSAGE;
        }
        try {
            telegramClient.sendMessage(message.chat().id(), reply);
        } catch (RuntimeException ex) {
            LOG.warn("Could not send message to Telegram: {}", ex.getMessage());
        }
    }
}

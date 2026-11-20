package ar.edu.unsam.ddso.tp.bot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "telegram.bot.token")
public class TelegramBotConfig {}

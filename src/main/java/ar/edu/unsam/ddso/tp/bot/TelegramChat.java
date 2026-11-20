package ar.edu.unsam.ddso.tp.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramChat(long id) {}
